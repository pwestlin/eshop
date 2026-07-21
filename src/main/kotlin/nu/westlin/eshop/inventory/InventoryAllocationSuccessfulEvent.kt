package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId

data class InventoryAllocationSuccessfulEvent(override val orderId: OrderId) : OrderEvent