package nu.westlin.eshop.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderIdWritingConverterTest {

    private val converter = OrderIdWritingConverter()

    @Test
    fun `convert value`() {
        val orderId = OrderId.generate()
        assertThat(converter.convert(orderId)).isEqualTo(orderId.value)
    }
}