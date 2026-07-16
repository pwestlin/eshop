package nu.westlin.eshop.payment.internal

import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.PaymentFailedEvent
import nu.westlin.eshop.common.PaymentSuccessfulEvent
import nu.westlin.eshop.common.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentProcessorService: PaymentProcessorService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @ApplicationModuleListener
    fun handleInventoryAllocationSuccessfulEvent(event: InventoryAllocationSuccessfulEvent) {
        // TODO pwestlin: Listeners for these events, in order and inventory
        runCatching { paymentProcessorService.processPayment(event.orderId) }.fold(
            { eventPublisher.publishEvent(PaymentSuccessfulEvent(event.orderId)) },
            { exception ->
                eventPublisher.publishEvent(
                    PaymentFailedEvent(
                        event.orderId,
                        exception.message ?: error("Should not been null"),
                    ),
                )
            },
        )
    }
}

@Service
class PaymentProcessorService {
    private val logger = logger()

    @Suppress("unused")
    fun processPayment(orderId: OrderId) {
        // TODO pwestlin: Do something fun to simulate an payment :)
        logger.info("Payment for order $orderId is accepted")
    }
}