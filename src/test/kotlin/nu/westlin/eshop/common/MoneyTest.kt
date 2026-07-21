package nu.westlin.eshop.common

import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class MoneyTest {

    @Test
    fun `wrong scale`() {
        assertThatThrownBy {
            Money.example(amount = BigDecimal.valueOf(4.123))
        }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Monetary amount scale cannot exceed 2 decimal places: amount: 4.123, scale: 3")
    }

    @DisplayName("compareTo")
    @Nested
    inner class MoneyCompareToTest {

        @Test
        fun `should be 0`() {
            val money1 = Money.example()
            val money2 = Money(
                amount = money1.amount,
                currency = money1.currency,
            )

            assertThat(money1).isEqualByComparingTo(money2)
        }

        @Test
        fun `should be greater than 0`() {
            val money1 = Money.example()
            val money2 = Money(
                amount = money1.amount - BigDecimal.valueOf(1),
                currency = money1.currency,
            )

            assertThat(money1).isGreaterThan(money2)
        }

        @Test
        fun `should be less than 0`() {
            val money1 = Money.example()
            val money2 = Money(
                amount = money1.amount + BigDecimal.valueOf(1),
                currency = money1.currency,
            )

            assertThat(money1).isLessThan(money2)
        }

        @Test
        fun `must have the same currency`() {
            val money1 = Money.example(currency = Currency.getInstance("NOK"))
            val money2 = Money(
                amount = money1.amount,
                currency = SEK,
            )

            assertThatThrownBy { money1.compareTo(money2) }
                .isExactlyInstanceOf<IllegalArgumentException>()
                .hasMessage("Currency mismatch: NOK vs SEK")
        }
    }
}