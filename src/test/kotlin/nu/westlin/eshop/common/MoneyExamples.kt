package nu.westlin.eshop.common

import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

fun Money.Companion.example(
    amount: BigDecimal = BigDecimal.valueOf(Random.nextLong(1, 1_000_000), 2),
    currency: Currency = SEK,
): Money = Money(
    amount = amount,
    currency = currency,
)