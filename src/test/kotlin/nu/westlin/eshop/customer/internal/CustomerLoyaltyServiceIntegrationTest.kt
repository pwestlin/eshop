package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderShippedEvent
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario
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
class CustomerLoyaltyServiceIntegrationTest @Autowired constructor(
    private val customerOrderSpringDataRepository: SpringDataCustomerOrderRepository,
) {

    @Test
    fun `should handle order shipped event`(scenario: Scenario) {
        val event = OrderShippedEvent(
            orderId = OrderId.generate(),
            customerId = CustomerId.generate(),
            totalPrice = 42,
            occurredAt = instantNowTruncated(),
        )

        val expectedCustomerOrder = CustomerOrder(
            customerId = event.customerId,
            orderId = event.orderId,
            totalPrice = event.totalPrice,
            instant = event.occurredAt,
        )

        scenario.publish(event)
            .andWaitForStateChange {
                customerOrderSpringDataRepository.findAllByCustomerIdAndInstantGreaterThanEqual(
                    event.customerId,
                    instantNowTruncated().minusSeconds(60),
                )
            }
            .andVerify { customerOrders ->
                assertThat(customerOrders)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .ignoringCollectionOrder()
                    .isEqualTo(listOf(expectedCustomerOrder))
            }
    }
}