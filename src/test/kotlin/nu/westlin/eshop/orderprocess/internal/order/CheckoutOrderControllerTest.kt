package nu.westlin.eshop.orderprocess.internal.order

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody

@WebMvcTest(CheckoutOrderController::class)
@AutoConfigureRestTestClient
@AutoConfigureMockMvc(addFilters = false)
class CheckoutOrderControllerTest(@Autowired private val client: RestTestClient) {

    @MockkBean
    private lateinit var checkoutOrderService: CheckoutOrderService

    @Test
    fun `checkout order - all is good`() {
        val request = CheckoutRequest.example()

        every {
            checkoutOrderService.processCheckout(
                orderId = OrderId(request.orderId),
                customerId = CustomerId(request.customerId),
                items = request.items,
            )
        } returns CheckoutResult.Success

        client
            .post()
            .uri("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().location("http://localhost/orders/${request.orderId}")
            .expectBody<CheckoutResponse>()
            .value { response ->
                assertThat(response).isEqualTo(CheckoutResponse(request.orderId))
            }
    }
}
