package nu.westlin.eshop.common

import java.time.Instant

data class OrderPlacedEvent(
    val orderId: OrderId,
    val customerId: CustomerId,
    val items: Set<OrderPlacedItem>,
    val occurredAt: Instant,
) {
    data class OrderPlacedItem(val productId: ProductId, val quantity: Int) {
        companion object
    }

    companion object
}
