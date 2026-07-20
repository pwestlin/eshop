package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.test.context.TestPropertySource

// When you run the test with Gradle you get 30 sec timeout after completed test suite and the below is to fix that...
@TestPropertySource(
    properties = [
        "spring.datasource.hikari.connection-timeout=2000",
        "spring.datasource.hikari.validation-timeout=1000",
    ],
)
@ApplicationModuleTest
@Import(SharedTestcontainersConfiguration::class)
class OrderStatusChangesServiceIntegrationTest @Autowired constructor(private val orderRepository: OrderRepository) {

/*    @Suppress("unused")
    @MockkBean
    private lateinit var customerService: CustomerService

    @Suppress("unused")
    @MockkBean
    private lateinit var catalogfacade: CatalogFacade

    @Test
    @Suppress("IgnoredReturnValue")
    fun `handle InventoryAllocationSuccessfulEvent`(scenario: Scenario) {
        val order = Order.example()
        orderRepository.insert(order)
        val event = InventoryAllocationSuccessfulEvent(order.id)

        scenario.publish(event)
            .andWaitForStateChange(
                { orderRepository.findById(order.id)?.status ?: OrderStatus.PENDING },
                { status -> status == OrderStatus.STOCKRESERVED },
            )
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `handle InventoryAllocationFailedEvent`(scenario: Scenario) {
        val order = Order.example()
        orderRepository.insert(order)
        val event = InventoryAllocationFailedEvent(
            orderId = order.id,
            tooFewProducts = setOf(
                InventoryAllocationFailedEvent.TooFewProducts(
                    productId = ProductId.generate(),
                    orderQuantity = 42,
                    inventoryQuantity = 7,
                ),
            ),
        )

        scenario.publish(event)
            .andWaitForStateChange(
                { orderRepository.findById(order.id)?.status ?: OrderStatus.PENDING },
                { status -> status == OrderStatus.CANCELLED },
            )
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `handle PaymentSuccessfulEvent`(scenario: Scenario) {
        val order = Order.example(status = OrderStatus.STOCKRESERVED)
        orderRepository.insert(order)
        val event = PaymentSuccessfulEvent(order.id)

        scenario.publish(event)
            .andWaitForStateChange(
                { orderRepository.findById(order.id)?.status ?: OrderStatus.STOCKRESERVED },
                { status -> status == OrderStatus.PAID },
            )
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `handle PaymentFailedEvent`(scenario: Scenario) {
        val order = Order.example(status = OrderStatus.STOCKRESERVED)
        orderRepository.insert(order)
        val event = PaymentFailedEvent(
            orderId = order.id,
            reason = "You poor bastard",
        )

        scenario.publish(event)
            .andWaitForStateChange(
                { orderRepository.findById(order.id)?.status ?: OrderStatus.STOCKRESERVED },
                { status -> status == OrderStatus.CANCELLED },
            )
    }

    @Test
    @Suppress("IgnoredReturnValue")
    fun `handle OrderShippedEvent`(scenario: Scenario) {
        val order = Order.example(status = OrderStatus.PAID)
        orderRepository.insert(order)
        val event = OrderShippedEvent(
            orderId = order.id,
            shippedTime = instantNowTruncated(),
        )

        scenario.publish(event)
            .andWaitForEventOfType(OrderCompletedEvent::class.java)
            .matching { event ->
                event.orderId == order.id
            }
            .toArriveAndVerify { event ->
                assertThat(event)
                    .usingRecursiveComparison()
                    .ignoringFields("occurredAt")
                    .isEqualTo(
                        OrderCompletedEvent(
                            orderId = order.id,
                            customerId = order.customerId,
                            totalPrice = order.totalPrice,
                            occurredAt = instantNowTruncated(),
                        ),
                    )

                val updatedOrder = orderRepository.findById(order.id)
                checkNotNull(updatedOrder) { "order with id ${order.id} is null" }

                assertThat(updatedOrder).isNotNull
                assertThat(updatedOrder.status).isEqualTo(OrderStatus.SHIPPED)
                assertThat(updatedOrder.shippedTime).isBeforeOrEqualTo(instantNowTruncated())
            }
    }*/

    @Test
    fun `dghgdh gjs`() {
        TODO()
    }
}