package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.OrderId
import java.time.Instant

data class OrderShippedEvent(val orderId: OrderId, val shippedTime: Instant) {

    companion object
}