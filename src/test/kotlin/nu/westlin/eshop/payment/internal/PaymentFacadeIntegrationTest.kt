package nu.westlin.eshop.payment.internal

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.inventory.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.payment.PaymentFailedEvent
import nu.westlin.eshop.payment.PaymentSuccessfulEvent
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "spring.datasource.hikari.connection-timeout=2000",
        "spring.datasource.hikari.validation-timeout=1000",
    ],
)
@ApplicationModuleTest
@Import(SharedTestcontainersConfiguration::class)
class PaymentFacadeIntegrationTest {

    @MockkBean
    private lateinit var paymentProcessor: PaymentProcessor

    @Test
    fun `handle InventoryAllocationSuccessfulEvent - ok`(scenario: Scenario) {
        val orderId = OrderId.generate()
        every { paymentProcessor.processPayment(orderId) } just runs

        val orderInventoryAllocationSuccessfulEvent = InventoryAllocationSuccessfulEvent(orderId)
        val paymentSuccessfulEvent = PaymentSuccessfulEvent(orderId)

        scenario.publish(orderInventoryAllocationSuccessfulEvent)
            .andWaitForEventOfType(PaymentSuccessfulEvent::class.java)
            .matching { event: PaymentSuccessfulEvent ->
                event == paymentSuccessfulEvent
            }
            .toArrive()
    }

    @Test
    fun `handle InventoryAllocationSuccessfulEvent - process fails`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val exception = RuntimeException("Not enough funds")
        every { paymentProcessor.processPayment(orderId) } throws exception

        val orderInventoryAllocationSuccessfulEvent = InventoryAllocationSuccessfulEvent(orderId)
        val paymentFailedEvent = PaymentFailedEvent(orderId, exception.message!!)

        scenario.publish(orderInventoryAllocationSuccessfulEvent)
            .andWaitForEventOfType(PaymentFailedEvent::class.java)
            .matching { event: PaymentFailedEvent ->
                event == paymentFailedEvent
            }
            .toArrive()
    }
}