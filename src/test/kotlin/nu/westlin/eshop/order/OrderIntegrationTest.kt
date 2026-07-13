package nu.westlin.eshop.order

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.example
import nu.westlin.eshop.customer.CustomerService
import nu.westlin.eshop.order.internal.checkout.CheckoutRequest
import nu.westlin.eshop.order.internal.checkout.CheckoutResponse
import nu.westlin.eshop.order.internal.checkout.OrderRepository
import nu.westlin.eshop.order.internal.checkout.ProcessCheckoutResult
import nu.westlin.eshop.order.internal.domain.OrderStatus
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.AssertablePublishedEvents
import org.springframework.modulith.test.Scenario
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody

// When you run the test with Gradle you get 30 sec timeout after completed test suite and the below is to fix that...
@TestPropertySource(
    properties = [
        "spring.datasource.hikari.connection-timeout=2000",
        "spring.datasource.hikari.validation-timeout=1000",
    ],
)
@ApplicationModuleTest
@AutoConfigureRestTestClient
@Import(SharedTestcontainersConfiguration::class)
class OrderIntegrationTest @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val client: RestTestClient,
) {

    @MockkBean
    private lateinit var customerService: CustomerService

    @Test
    fun `should process checkout, store order and publish OrderPlacedEvent`(scenario: Scenario) {
        val checkoutRequest = CheckoutRequest.example()
        val orderId = OrderId(checkoutRequest.orderId)
        val customerId = CustomerId(checkoutRequest.customerId)

        every { customerService.exists(customerId) } returns true

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

    @Test
    fun `should not process checkout because customer with customerId does not exist`(
        scenario: Scenario,
        events: AssertablePublishedEvents,
    ) {
        val checkoutRequest = CheckoutRequest.example()
        val orderId = OrderId(checkoutRequest.orderId)
        val customerId = CustomerId(checkoutRequest.customerId)

        every { customerService.exists(customerId) } returns false

        scenario.stimulate {
            client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(checkoutRequest)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectBody<ProcessCheckoutResult.CustomerDoesNotExist>()
                .value { response ->
                    assertThat(response).isEqualTo(ProcessCheckoutResult.CustomerDoesNotExist(customerId))
                }
        }

        assertThat(orderRepository.findById(orderId)).isNull()

        assertThat(events.ofType(OrderPlacedEvent::class.java)).isEmpty()
    }
}