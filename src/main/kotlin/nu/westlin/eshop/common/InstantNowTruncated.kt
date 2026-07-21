package nu.westlin.eshop.common

import java.time.Instant
import java.time.temporal.ChronoUnit

// TODO pwestlin: Vad ska denna egentligen heta (instantNowTruncated, truncatedInstantNow)?
fun instantNowTruncated(chronoUnit: ChronoUnit = ChronoUnit.MICROS): Instant = Instant.now().truncatedTo(chronoUnit)