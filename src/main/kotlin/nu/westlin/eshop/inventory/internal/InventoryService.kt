package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.InventoryAllocationFailedEvent
import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.OrderShippedEvent
import nu.westlin.eshop.common.PaymentSuccessfulEvent
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryItemRepository: InventoryItemRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val logger = logger()

    @ApplicationModuleListener
    fun handleOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        logger.info("Order placed: $orderPlacedEvent")
        val tooFewProducts = reserveProducts(orderPlacedEvent.items)
        if (tooFewProducts.isEmpty()) {
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
        eventPublisher.publishEvent(
            OrderShippedEvent(
                orderId = paymentSuccessfulEvent.orderId,
                shippedTime = instantNowTruncated(),
            ),
        )
        logger.info("Order ${paymentSuccessfulEvent.orderId} shipped")
    }

    private fun reserveProducts(orderedItems: Set<OrderPlacedEvent.OrderPlacedItem>): Set<TooFewProducts> {
        // TODO pwestlin: Lägg till i en lista av reserverade produkter
        return orderedItems.mapNotNull { orderedItem ->
            val inventoryItem = inventoryItemRepository.getById(orderedItem.productId)
            if (inventoryItem.quantity >= orderedItem.quantity) {
                null
            } else {
                TooFewProducts(
                    productId = orderedItem.productId,
                    orderQuantity = orderedItem.quantity,
                    inventoryQuantity = inventoryItem.quantity,
                )
            }
        }.toSet()
    }

    private data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
}