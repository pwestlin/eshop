package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.orderprocess.internal.order.CheckoutRequest
import nu.westlin.eshop.orderprocess.internal.order.example
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@JsonTest
class CheckoutRequestJsonTest(@Autowired private val jsonMapper: JsonMapper) {

    @Test
    fun `serialize and deserialize`() {
        val request = CheckoutRequest.example()

        val json = jsonMapper.writeValueAsString(request)
        assertThat(jsonMapper.readValue<CheckoutRequest>(json)).isEqualTo(request)
    }
}