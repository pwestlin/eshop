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

data class InventoryAllocationSuccessfulEvent(val orderId: OrderId)

data class InventoryAllocationFailedEvent(val orderId: OrderId, val tooFewProducts: Set<TooFewProducts>) {
    data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
}

data class OrderShippedEvent(
    val orderId: OrderId,
    val customerId: CustomerId,
    val totalPrice: Int,
    val occurredAt: Instant,
) {
    data class OrderPlacedItem(val productId: ProductId, val quantity: Int) {
        companion object
    }

    companion object
}
