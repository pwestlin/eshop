package nu.westlin.eshop.order

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nu.westlin.eshop.catalog.CatalogService
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.example
import nu.westlin.eshop.customer.CustomerDiscountDto
import nu.westlin.eshop.customer.CustomerService
import nu.westlin.eshop.customer.Percentage
import nu.westlin.eshop.order.internal.checkout.CheckoutRequest
import nu.westlin.eshop.order.internal.checkout.CheckoutRequest.CheckoutItemRequest
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

    @MockkBean
    private lateinit var catalogService: CatalogService

    @Test
    fun `should process checkout, apply 10 percent discount, store order and publish OrderPlacedEvent`(
        scenario: Scenario,
    ) {
        val checkoutRequest = CheckoutRequest.example()
        val orderId = OrderId(checkoutRequest.orderId)
        val customerId = CustomerId(checkoutRequest.customerId)

        every { customerService.exists(customerId) } returns true
        checkoutRequest.items.forEach { item ->
            every { catalogService.exists(ProductId(item.productId)) } returns true
        }
        val customerDiscountDto = CustomerDiscountDto(
            tier = "42",
            rate = Percentage(0.1),
        )
        every { customerService.discount(customerId) } returns customerDiscountDto

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
            .toArriveAndVerify { orderPlacedEvent ->
                val storedOrder = orderRepository.findById(orderPlacedEvent.orderId)

                requireNotNull(storedOrder)
                assertThat(storedOrder.customerId).isEqualTo(customerId)
                assertThat(storedOrder.status).isEqualTo(OrderStatus.Pending)

                // TODO pwestlin: Kontrollera items
                val expectedSubTotal = checkoutRequest.items.sumOf { it.price * it.quantity }
                assertThat(storedOrder.subTotal).isEqualTo(expectedSubTotal)
                assertThat(storedOrder.discount).isEqualTo(customerDiscountDto.rate)
                val expectedTotalPrice = (expectedSubTotal * (1.toDouble() - customerDiscountDto.rate.fraction)).toInt()
                assertThat(storedOrder.totalPrice).isEqualTo(expectedTotalPrice)
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

    @Test
    fun `should not process checkout because two productIds does not exist`(
        scenario: Scenario,
        events: AssertablePublishedEvents,
    ) {
        val productId1 = 5
        val productId2 = 42
        val productId3 = 69
        val items = setOf(
            CheckoutItemRequest(
                productId = productId1,
                quantity = 5,
                price = 5,
            ),
            CheckoutItemRequest(
                productId = productId2,
                quantity = 42,
                price = 42,
            ),
            CheckoutItemRequest(
                productId = productId3,
                quantity = 69,
                price = 69,
            ),
        )
        val checkoutRequest = CheckoutRequest.example(items = items)
        val orderId = OrderId(checkoutRequest.orderId)
        val customerId = CustomerId(checkoutRequest.customerId)

        every { customerService.exists(customerId) } returns true

        every { catalogService.exists(ProductId(productId1)) } returns true
        every { catalogService.exists(ProductId(productId2)) } returns false
        every { catalogService.exists(ProductId(productId3)) } returns false

        scenario.stimulate {
            client.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(checkoutRequest)
                .exchange()
                .expectStatus().isBadRequest
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectBody<ProcessCheckoutResult.ProductsDoesNotExist>()
                .value { response ->
                    assertThat(response).isEqualTo(
                        ProcessCheckoutResult.ProductsDoesNotExist(
                            setOf(
                                ProductId(productId2),
                                ProductId(productId3),
                            ),
                        ),
                    )
                }
        }

        assertThat(orderRepository.findById(orderId)).isNull()

        assertThat(events.ofType(OrderPlacedEvent::class.java)).isEmpty()
    }
}