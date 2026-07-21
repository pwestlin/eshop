package nu.westlin.eshop.order.internal

import nu.westlin.eshop.common.OrderEvent
import nu.westlin.eshop.common.logger
import nu.westlin.eshop.order.internal.checkout.OrderRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.modulith.events.FailedEventPublications
import org.springframework.modulith.events.ResubmissionOptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Component
class OrderEventRetryScheduler(
    private val failedEvents: FailedEventPublications,
    private val orderRepository: OrderRepository,
    private val jdbcClient: JdbcClient,
) {
    private val logger = logger()

    companion object {
        private const val MAX_RETRIES = 5
    }

    @Scheduled(fixedRateString = "PT1M")
    @Transactional
    fun processFailedEvents() {
        val options = ResubmissionOptions.defaults()
            .withMinAge(Duration.ofMinutes(1))
            .withFilter { publication ->
                val event = publication.event

                // 1. Ignorera allt som INTE är ett OrderEvent
                if (event !is OrderEvent) {
                    return@withFilter false
                }
                logger.info("publication.completionAttempts: ${publication.completionAttempts}")
                // 2. Hantera terminalt fel om max gränsen nåtts
                if (publication.completionAttempts >= MAX_RETRIES) {
                    handleTerminalFailure(publication, event)
                    return@withFilter false
                }

                logger.info(
                    "Generell retry på event [ID: {}] av typ {}. Tidigare försök: {}.",
                    publication.identifier,
                    event::class.simpleName,
                    publication.completionAttempts,
                )

                true // Gör retry via Spring Modulith
            }

        failedEvents.resubmit(options)
    }

    private fun handleTerminalFailure(
        publication: org.springframework.modulith.events.EventPublication,
        event: OrderEvent,
    ) {
        logger.error(
            "Event {} för order {} misslyckades permanent efter {} försök.",
            event::class.simpleName,
            event.orderId,
            publication.completionAttempts,
        )

        orderRepository.findById(event.orderId)?.let { order ->
            orderRepository.update(order.fail())
        } ?: error("Order with id ${event.orderId} does not exist")

        // TODO pwestlin: Create repo for event_publication?
        jdbcClient.sql("UPDATE event_publication SET completion_date = ? WHERE id = ?")
            .param(Instant.now())
            .param(publication.identifier)
            .update()
    }
}