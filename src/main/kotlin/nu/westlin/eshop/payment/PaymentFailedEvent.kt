package nu.westlin.eshop.payment

import nu.westlin.eshop.common.OrderId

data class PaymentFailedEvent(val orderId: OrderId, val reason: String)