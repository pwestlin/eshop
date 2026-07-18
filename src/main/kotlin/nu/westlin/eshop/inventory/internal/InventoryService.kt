package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.InventoryAllocationFailedEvent
import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderCancelledEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.OrderShippedEvent
import nu.westlin.eshop.common.PaymentSuccessfulEvent
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryService(
    private val reserveProductsService: ReserveProductsService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val logger = logger()

    @ApplicationModuleListener
    fun handleOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        logger.info("Order placed: $orderPlacedEvent")
        val tooFewProducts = reserveProductsService.areProductsAvailable(orderPlacedEvent.items)
        if (tooFewProducts.isEmpty()) {
            reserveProductsService.reserveProducts(orderPlacedEvent.orderId, orderPlacedEvent.items)
            eventPublisher.publishEvent(InventoryAllocationSuccessfulEvent(orderPlacedEvent.orderId))
        } else {
            eventPublisher.publishEvent(
                InventoryAllocationFailedEvent(
                    orderId = orderPlacedEvent.orderId,
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

        // TODO pwestlin: Vad göra om det kastas ett exception?
    }

    @ApplicationModuleListener
    fun handlePaymentSuccessfulEvent(paymentSuccessfulEvent: PaymentSuccessfulEvent) {
        reserveProductsService.completeReservation(paymentSuccessfulEvent.orderId)
        eventPublisher.publishEvent(
            OrderShippedEvent(
                orderId = paymentSuccessfulEvent.orderId,
                shippedTime = instantNowTruncated(),
            ),
        )
        logger.info("Order ${paymentSuccessfulEvent.orderId} shipped")
    }

    // TODO pwestlin: Testa
    @ApplicationModuleListener
    fun handleOrderCancelledEvent(event: OrderCancelledEvent) {
        reserveProductsService.deleteReservation(event.orderId)
    }
}

// TODO pwestlin: Bad name
@Service
class ReserveProductsService(
    private val inventoryItemRepository: InventoryItemRepository,
    private val reservedInventoryItemRepository: ReservedInventoryItemRepository,
) {

    fun areProductsAvailable(orderedItems: Set<OrderPlacedEvent.OrderPlacedItem>): Set<TooFewProducts> =
        orderedItems.mapNotNull { orderedItem ->
            val inventoryItem = inventoryItemRepository.getById(orderedItem.productId)
            val reservedQuantity =
                reservedInventoryItemRepository.findByProductId(orderedItem.productId).sumOf { it.quantity }
            val actualQuantityInStock = inventoryItem.quantity - reservedQuantity
            if (actualQuantityInStock >= orderedItem.quantity) {
                null
            } else {
                TooFewProducts(
                    productId = orderedItem.productId,
                    orderQuantity = orderedItem.quantity,
                    inventoryQuantity = actualQuantityInStock,
                )
            }
        }.toSet()

    @Transactional
    fun reserveProducts(orderId: OrderId, orderedItems: Set<OrderPlacedEvent.OrderPlacedItem>) {
        orderedItems.forEach { item ->
            reservedInventoryItemRepository.insert(
                ReservedInventoryItem(
                    id = ReservedInventoryItemId(
                        productId = item.productId,
                        orderId = orderId,
                    ),
                    quantity = item.quantity,
                ),
            )
        }
    }

    // TODO pwestlin: Bad name
    @Transactional
    fun completeReservation(orderId: OrderId) {
        val reservedItems = reservedInventoryItemRepository.findByOrderId(orderId)
        reservedItems.forEach { reservedItem ->
            val item = inventoryItemRepository.getById(reservedItem.id.productId)
            inventoryItemRepository.update(item.copy(quantity = item.quantity - reservedItem.quantity))
            reservedInventoryItemRepository.delete(reservedItem)
        }
    }

    @Transactional
    fun deleteReservation(orderId: OrderId) {
        reservedInventoryItemRepository.findByOrderId(orderId).forEach { item ->
            reservedInventoryItemRepository.delete(item)
        }
    }
}

data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
