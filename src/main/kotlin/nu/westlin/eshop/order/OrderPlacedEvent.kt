package nu.westlin.eshop.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import java.time.Instant

data class OrderPlacedEvent(
    override val orderId: OrderId,
    val customerId: CustomerId,
    val items: Set<OrderPlacedItem>,
    val occurredAt: Instant,
) : OrderEvent {
    data class OrderPlacedItem(val productId: ProductId, val quantity: Int) {
        companion object
    }

    companion object
}