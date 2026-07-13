package nu.westlin.eshop.order.internal.checkout

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.example
import nu.westlin.eshop.order.internal.domain.Order
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import java.util.*

@WebMvcTest(CheckoutController::class)
@AutoConfigureRestTestClient
class CheckoutControllerTest(@Autowired private val client: RestTestClient) {

    @MockkBean
    private lateinit var checkoutService: CheckoutService

    @Test
    fun `create order id`() {
        client
            .get()
            .uri("/orders/id/create")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<UUID>()
            .value { uuid ->
                // Här är 'uuid' automatiskt en icke-nullable UUID (smart castad av AssertJ-blocket)
                assertThat(uuid).isNotNull()
            }
    }

    @Test
    fun `checkout order`() {
        val request = CheckoutRequest.example()

        val createdOrder = Order.new(
            id = OrderId(request.orderId),
            customerId = CustomerId(request.customerId),
            items = request.toDomainItems(),
        )

        every {
            checkoutService.processCheckout(
                orderId = OrderId(request.orderId),
                customerId = CustomerId(request.customerId),
                items = request.toDomainItems(),
            )
        } returns createdOrder

        client
            .post()
            .uri("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().location("http://localhost/orders/${createdOrder.id.value}")
            .expectBody<CheckoutResponse>()
            .value { response ->
                // Här är 'uuid' automatiskt en icke-nullable UUID (smart castad av AssertJ-blocket)
                assertThat(response).isEqualTo(CheckoutResponse(createdOrder.id.value))
            }
    }
}
