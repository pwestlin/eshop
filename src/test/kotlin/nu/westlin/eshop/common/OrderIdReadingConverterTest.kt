package nu.westlin.eshop.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderIdReadingConverterTest {

    private val converter = OrderIdReadingConverter()

    @Test
    fun `convert value`() {
        val orderId = OrderId.generate()
        assertThat(converter.convert(orderId.value)).isEqualTo(orderId)
    }
}