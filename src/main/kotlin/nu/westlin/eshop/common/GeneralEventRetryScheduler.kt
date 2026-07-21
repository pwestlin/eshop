package nu.westlin.eshop.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.modulith.events.FailedEventPublications
import org.springframework.modulith.events.ResubmissionOptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Handles retries for failed events.
 */
@Component
class GeneralEventRetryScheduler(
    private val failedEvents: FailedEventPublications,
    @Value($$"${events.minAge}") private val minAge: Duration,
    @Value($$"${events.maxAttempts}") private val maxAttempts: Int,
) {
    private val logger = logger()

    @Scheduled(fixedRateString = "PT1M")
    fun resubmitGenericFailedEvents() {
        val options = ResubmissionOptions.defaults()
            .withMinAge(minAge)
            .withFilter { publication ->
                val event = publication.event

                // Ignorera alla OrderEvents – de ägs och hanteras av order-modulen
                if (event is OrderEvent) {
                    return@withFilter false
                }

                val shouldRetry = publication.completionAttempts < maxAttempts

                if (shouldRetry) {
                    logger.info(
                        "Generell retry på event [ID: {}] av typ {}. Tidigare försök: {}.",
                        publication.identifier,
                        event::class.simpleName,
                        publication.completionAttempts,
                    )
                } else {
                    logger.error(
                        "Generellt event [ID: {}] av typ {} har nått max antal försök ({}) och kräver manuell översyn.",
                        publication.identifier,
                        event::class.simpleName,
                        publication.completionAttempts,
                    )
                }

                shouldRetry
            }

        failedEvents.resubmit(options)
    }
}