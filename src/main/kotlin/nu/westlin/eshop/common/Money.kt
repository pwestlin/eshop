package nu.westlin.eshop.common

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

data class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    init {
        require(
            amount.scale() <= 2,
        ) { "Monetary amount scale cannot exceed 2 decimal places: amount: $amount, scale: ${amount.scale()}" }
    }

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount.add(other.amount), currency)
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount.subtract(other.amount), currency)
    }

    operator fun times(multiplicand: Int): Money = Money(amount.multiply(BigDecimal(multiplicand)), currency)

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return amount.compareTo(other.amount)
    }

    operator fun times(percentage: Percentage): Money {
        val factor = BigDecimal.valueOf(percentage.fraction)
        val calculatedAmount = amount.multiply(factor).setScale(2, RoundingMode.HALF_UP)
        return Money(calculatedAmount, currency)
    }

    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) {
            "Currency mismatch: $currency vs ${other.currency}"
        }
    }

    companion object {
        fun sek(amount: BigDecimal): Money = Money(
            amount.setScale(2, RoundingMode.HALF_UP),
            SEK,
        )

        fun sek(amount: Long): Money = sek(BigDecimal.valueOf(amount))

        fun zero(currency: Currency = SEK): Money = Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), currency)
    }
}

val SEK: Currency = Currency.getInstance("SEK")
