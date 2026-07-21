package nu.westlin.eshop.payment

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId

data class PaymentFailedEvent(override val orderId: OrderId, val reason: String) : OrderEvent