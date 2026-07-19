package nu.westlin.eshop.payment

import nu.westlin.eshop.common.OrderId

data class PaymentSuccessfulEvent(val orderId: OrderId)