package nu.westlin.eshop.common

import java.time.Instant
import java.time.temporal.ChronoUnit

fun instantNowTruncated(chronoUnit: ChronoUnit = ChronoUnit.MICROS): Instant = Instant.now().truncatedTo(chronoUnit)