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
class EventRetryScheduler(
    private val failedEvents: FailedEventPublications,
    @Value($$"${events.minAge}") private val minAge: Duration,
    @Value($$"${events.maxAttempts}") private val maxAttempts: Int,
) {
    private val logger = logger()

    // Körs t.ex. varje minut för att försöka köra om misslyckade events
    @Scheduled(fixedRateString = "PT1M")
    fun resubmitFailedEvents() {
        val options = ResubmissionOptions.defaults()
            .withMinAge(minAge)
            .withFilter { publication ->
                val retry = publication.completionAttempts < maxAttempts
                logger.info(
                    "Gör retry på misslyckat event [ID: {}] av typ {}. Tidigare försök: {}. Ursprungligen publicerat: {}",
                    publication.identifier,
                    publication.event::class.simpleName,
                    publication.completionAttempts,
                    publication.publicationDate
                )
                retry
            }

        // Återpublicerar endast events som har försökts färre än maxAttempts gånger
        failedEvents.resubmit(options)
    }
}