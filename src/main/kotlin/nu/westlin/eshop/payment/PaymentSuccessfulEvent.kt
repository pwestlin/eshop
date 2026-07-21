package nu.westlin.eshop.payment

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.OrderId

data class PaymentSuccessfulEvent(override val orderId: OrderId) : OrderEvent