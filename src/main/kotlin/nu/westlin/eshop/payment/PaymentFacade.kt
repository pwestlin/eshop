package nu.westlin.eshop.payment

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.payment.internal.PaymentProcessorService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentFacade(
    private val paymentProcessorService: PaymentProcessorService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Suppress("unused")
    @Transactional
    fun processPayment(orderId: OrderId, customerId: CustomerId, totalAmount: Int) {
        runCatching { paymentProcessorService.processPayment(orderId) }.fold(
            { eventPublisher.publishEvent(PaymentSuccessfulEvent(orderId)) },
            { exception ->
                eventPublisher.publishEvent(
                    PaymentFailedEvent(
                        orderId,
                        exception.message ?: error("Should not have been null"),
                    ),
                )
            },
        )

    }
}