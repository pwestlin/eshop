package nu.westlin.eshop.payment.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.logger
import org.springframework.stereotype.Component

@Component
class PaymentProcessor {
    private val logger = logger()

    @Suppress("unused")
    fun processPayment(orderId: OrderId) {
        // Do something fun to simulate an payment :)
        logger.info("Payment for order $orderId is accepted")
    }
}