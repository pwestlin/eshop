package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.order.internal.checkout.CheckoutRequest.CheckoutItemRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.util.*

@JsonTest
class CheckoutRequestJsonTest(@Autowired private val jsonMapper: JsonMapper) {

    @Test
    fun `serialize and deserialize`() {
        // TODO pwestlin: .example()
        val request = CheckoutRequest(
            orderId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            items = setOf(
                CheckoutItemRequest(
                    productId = UUID.randomUUID(),
                    quantity = 42,
                    price = 7,
                ),
            ),
        )

        val json = jsonMapper.writeValueAsString(request)
        println(json)
        assertThat(jsonMapper.readValue<CheckoutRequest>(json)).isEqualTo(request)
    }
}