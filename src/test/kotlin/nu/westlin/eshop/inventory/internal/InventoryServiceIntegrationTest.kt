package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.inventory.InventoryAllocationFailedEvent
import nu.westlin.eshop.inventory.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.order.OrderPlacedEvent
import nu.westlin.eshop.order.OrderPlacedEvent.OrderPlacedItem
import nu.westlin.eshop.inventory.OrderShippedEvent
import nu.westlin.eshop.payment.PaymentSuccessfulEvent
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.orderprocess.internal.order.example
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
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
class InventoryServiceIntegrationTest @Autowired private constructor(
    private val inventoryItemRepository: InventoryItemRepository,
) {

/*    @Test
    fun `handle OrderPlacedEvent - ok`(scenario: Scenario) {
        val orderPlacedEvent = OrderPlacedEvent.example()
        orderPlacedEvent.items.forEach { item ->
            inventoryItemRepository.insert(
                InventoryItem(
                    productId = item.productId,
                    quantity = item.quantity,
                ),
            )
        }

        scenario.publish(orderPlacedEvent)
            .andWaitForEventOfType(InventoryAllocationSuccessfulEvent::class.java)
            .matching { event: InventoryAllocationSuccessfulEvent ->
                event == InventoryAllocationSuccessfulEvent(
                    orderPlacedEvent.orderId,
                )
            }
            .toArrive()
    }

    @Test
    fun `handle OrderPlacedEvent - product not found`(@Suppress("UnusedParameter", "unused") scenario: Scenario) {
        // TODO pwestlin: Jag vet inte hur jag ska lösa detta fall än.
        assertThat(true).isTrue
        *//*
                val orderPlacedEvent = OrderPlacedEvent(
                    orderId = OrderId.generate(),
                    customerId = CustomerId.generate(),
                    items = setOf(
                        OrderPlacedItem(
                            productId = ProductId.generate(),
                            quantity = Random.nextInt(1, 50)
                        )
                    ),
                    occurredAt = instantNowTruncated()
                )
                scenario.publish(orderPlacedEvent)
                            .andWaitForStateChange {
                                // Vi väntar på att det ska dyka upp en icke-slutförd (failed/incomplete) publikation
                                incompletePublications.resubmitIncompletePublications { true }
                            }
                            .andVerify {
                                // Här verifierar vi att vår eventpublikation ligger kvar som "incomplete"
                                // eftersom lyssnaren kraschade och transaktionen rullades tillbaka.
                                val outstanding = incompletePublications.findIncompletePublications()
                                assertThat(outstanding).isNotEmpty()

                                // Om du vill vara helt säker på att det var just FooEvent som misslyckades:
                                val hasFooEvent = outstanding.any { it.event is FooEvent }
                                assertThat(hasFooEvent).isTrue()
                            }
         *//*
    }

    // TODO pwestlin: testa med reserverade
    @Test
    fun `handle OrderPlacedEvent - none is reserved - two of three products does not have enough quantity in inventory`(
        scenario: Scenario,
    ) {
        val item1 = OrderPlacedItem(
            productId = ProductId.generate(),
            quantity = 5,
        )
        val inventoryItem1 = InventoryItem(
            productId = item1.productId,
            quantity = item1.quantity - 1,
        )
        inventoryItemRepository.insert(inventoryItem1)

        val item2 = OrderPlacedItem(
            productId = ProductId.generate(),
            quantity = 7,
        )
        val inventoryItem2 = InventoryItem(
            productId = item2.productId,
            quantity = item2.quantity,
        )
        inventoryItemRepository.insert(inventoryItem2)

        val item3 = OrderPlacedItem(
            productId = ProductId.generate(),
            quantity = 9,
        )
        val inventoryItem3 = InventoryItem(
            productId = item3.productId,
            quantity = item3.quantity - 1,
        )
        inventoryItemRepository.insert(inventoryItem3)

        val orderPlacedEvent = OrderPlacedEvent.example(items = setOf(item1, item2, item3))

        scenario.publish(orderPlacedEvent)
            .andWaitForEventOfType(InventoryAllocationFailedEvent::class.java)
            .matching { event: InventoryAllocationFailedEvent ->
                event == InventoryAllocationFailedEvent(
                    orderId = orderPlacedEvent.orderId,
                    tooFewProducts = setOf(
                        InventoryAllocationFailedEvent.TooFewProducts(
                            productId = item1.productId,
                            orderQuantity = item1.quantity,
                            inventoryQuantity = inventoryItem1.quantity,
                        ),
                        InventoryAllocationFailedEvent.TooFewProducts(
                            productId = item3.productId,
                            orderQuantity = item3.quantity,
                            inventoryQuantity = inventoryItem3.quantity,
                        ),
                    ),
                )
            }
            .toArrive()
    }

    @Test
    fun `handle PaymentSuccessfulEvent`(scenario: Scenario) {
        val paymentSuccessfulEvent = PaymentSuccessfulEvent(OrderId.generate())

        scenario.publish(paymentSuccessfulEvent)
            .andWaitForEventOfType(OrderShippedEvent::class.java)
            .matching { event: OrderShippedEvent ->
                event.orderId == paymentSuccessfulEvent.orderId
            }
            .toArrive()
    }*/

    @Test
    fun `fghsdj d dghkh`() {
        TODO()
    }
}