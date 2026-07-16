package nu.westlin.eshop.common

import java.time.Instant

// TODO pwestlin: Lägg alla events i ett sealed interface (Application)Events?

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
    init {
        require(tooFewProducts.isNotEmpty()) { "tooFewProducts can not be empty" }
    }
    data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
}

data class OrderShippedEvent(val orderId: OrderId, val shippedTime: Instant) {

    companion object
}

data class OrderCompletedEvent(
    val orderId: OrderId,
    val customerId: CustomerId,
    val totalPrice: Int,
    val occurredAt: Instant,
) {

    companion object
}

data class PaymentSuccessfulEvent(val orderId: OrderId)

data class PaymentFailedEvent(val orderId: OrderId, val reason: String)