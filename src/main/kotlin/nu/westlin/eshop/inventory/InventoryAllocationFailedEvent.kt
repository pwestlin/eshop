package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId

data class InventoryAllocationFailedEvent(override val orderId: OrderId, val tooFewProducts: Set<TooFewProducts>) :
    OrderEvent {
    init {
        require(tooFewProducts.isNotEmpty()) { "tooFewProducts can not be empty" }
    }

    data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
}