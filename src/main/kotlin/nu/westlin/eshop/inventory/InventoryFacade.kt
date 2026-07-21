package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.inventory.internal.ReserveProductsService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InventoryFacade(
    private val reserveProductsService: ReserveProductsService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun reserveProducts(reservation: ProductsReservation) {
        val (orderId, items) = reservation
        val tooFewProducts = reserveProductsService.areProductsAvailable(reservation.items)
        if (tooFewProducts.isEmpty()) {
            reserveProductsService.reserveProducts(orderId, items)
            eventPublisher.publishEvent(InventoryAllocationSuccessfulEvent(orderId))
        } else {
            eventPublisher.publishEvent(
                InventoryAllocationFailedEvent(
                    orderId = orderId,
                    tooFewProducts = tooFewProducts.map { domain ->
                        InventoryAllocationFailedEvent.TooFewProducts(
                            productId = domain.productId,
                            orderQuantity = domain.orderQuantity,
                            inventoryQuantity = domain.inventoryQuantity,
                        )
                    }.toSet(),
                ),
            )
        }
    }

    @Transactional
    fun releaseForShipping(orderId: OrderId) {
        eventPublisher.publishEvent(OrderShippedEvent(orderId, instantNowTruncated()))
    }

    @Transactional
    fun completeReservation(orderId: OrderId) {
        reserveProductsService.completeReservation(orderId)
    }

    @Transactional
    fun cancelReservation(orderId: OrderId) {
        reserveProductsService.cancelReservation(orderId)
    }
}

data class ProductsReservation(val orderId: OrderId, val items: Set<Item>) {

    data class Item(val productId: ProductId, val quantity: Int) {
        companion object
    }

    companion object
}