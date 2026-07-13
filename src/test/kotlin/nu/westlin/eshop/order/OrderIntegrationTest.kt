package nu.westlin.eshop.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.example
import nu.westlin.eshop.order.internal.checkout.CheckoutRequest
import nu.westlin.eshop.order.internal.checkout.CheckoutResponse
import nu.westlin.eshop.order.internal.checkout.OrderRepository
import nu.westlin.eshop.order.internal.domain.OrderStatus
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody

@ApplicationModuleTest
@AutoConfigureRestTestClient
@Import(SharedTestcontainersConfiguration::class)
class OrderIntegrationTest @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val client: RestTestClient,
) {

    @Test
    fun `should process checkout, store order and publish OrderPlacedEvent`(scenario: Scenario) {
        val checkoutRequest = CheckoutRequest.example()
        val orderId = OrderId(checkoutRequest.orderId)
        val customerId = CustomerId(checkoutRequest.customerId)

        scenario.stimulate {
            client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(checkoutRequest)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().location("http://localhost/orders/${orderId.value}")
                .expectBody<CheckoutResponse>()
                .value { response ->
                    assertThat(response).isEqualTo(CheckoutResponse(orderId.value))
                }
        }
            .andWaitForEventOfType(OrderPlacedEvent::class.java)
            .matching { event -> event.orderId == orderId }
            .toArriveAndVerify {
                println("it = $it")
                val all = orderRepository.findAll()
                println("all = $all")

                val storedOrder = orderRepository.findById(orderId)

                assertThat(storedOrder).isNotNull
                assertThat(storedOrder?.customerId).isEqualTo(customerId)
                assertThat(storedOrder?.status).isEqualTo(OrderStatus.Pending)
            }
    }
}