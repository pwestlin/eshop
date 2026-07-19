package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderId

data class InventoryAllocationSuccessfulEvent(val orderId: OrderId)