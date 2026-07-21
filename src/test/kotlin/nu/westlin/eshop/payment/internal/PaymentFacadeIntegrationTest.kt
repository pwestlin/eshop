package nu.westlin.eshop.payment.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.payment.PaymentFacade
import nu.westlin.eshop.payment.PaymentSuccessfulEvent
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
class PaymentFacadeIntegrationTest @Autowired constructor(private val paymentFacade: PaymentFacade) {

    @Test
    fun `process payment`(scenario: Scenario) {
        val orderId = OrderId.generate()

        scenario
            .stimulate {
                paymentFacade.processPayment(
                    orderId = orderId,
                    customerId = CustomerId.generate(),
                    totalAmount = Money.sek(667),
                )
            }
            .andWaitForEventOfType(PaymentSuccessfulEvent::class.java)
            .matching { event: PaymentSuccessfulEvent ->
                event == PaymentSuccessfulEvent(orderId)
            }
            .toArrive()
    }
}