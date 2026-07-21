package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId
import java.time.Instant

data class OrderShippedEvent(override val orderId: OrderId, val shippedTime: Instant) : OrderEvent {

    companion object
}