package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DiscountTierTest {

    @DisplayName("fromTotalSum")
    @Nested
    inner class DiscountTierFromTotalSumTest {

        @Test
        fun fromTotalSum() {
            listOf(
                MoneyAndExpectedTier(Money.sek(0), DiscountTier.NONE),
                MoneyAndExpectedTier(Money.sek(500), DiscountTier.NONE),
                MoneyAndExpectedTier(Money.sek(5_000), DiscountTier.NONE),
                MoneyAndExpectedTier(Money.sek(9_999), DiscountTier.NONE),
                MoneyAndExpectedTier(Money.sek(10_000), DiscountTier.BRONZE),
                MoneyAndExpectedTier(Money.sek(24_999), DiscountTier.BRONZE),
                MoneyAndExpectedTier(Money.sek(25_000), DiscountTier.SILVER),
                MoneyAndExpectedTier(Money.sek(99_999), DiscountTier.SILVER),
                MoneyAndExpectedTier(Money.sek(100_000), DiscountTier.GOLD),
            ).forEach { data ->
                assertThat(DiscountTier.fromTotalSum(data.money)).isEqualTo(data.tier)
            }
        }
    }

    private data class MoneyAndExpectedTier(val money: Money, val tier: DiscountTier)
}