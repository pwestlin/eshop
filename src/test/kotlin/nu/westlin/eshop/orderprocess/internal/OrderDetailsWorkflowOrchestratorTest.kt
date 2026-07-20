package nu.westlin.eshop.orderprocess.internal

import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifySequence
import nu.westlin.eshop.catalog.CatalogFacade
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.example
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.customer.CustomerFacade
import nu.westlin.eshop.inventory.InventoryAllocationFailedEvent
import nu.westlin.eshop.inventory.InventoryAllocationFailedEvent.TooFewProducts
import nu.westlin.eshop.inventory.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.inventory.InventoryFacade
import nu.westlin.eshop.inventory.OrderShippedEvent
import nu.westlin.eshop.order.OrderFacade
import nu.westlin.eshop.order.OrderPlacedEvent
import nu.westlin.eshop.order.PaymentDetails
import nu.westlin.eshop.payment.PaymentFacade
import nu.westlin.eshop.payment.PaymentFailedEvent
import nu.westlin.eshop.payment.PaymentSuccessfulEvent
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario

@Suppress("IgnoredReturnValue")
@ApplicationModuleTest
@Import(SharedTestcontainersConfiguration::class)
class OrderDetailsWorkflowOrchestratorTest {

    @MockkBean
    private lateinit var inventoryFacade: InventoryFacade

    @MockkBean
    private lateinit var orderFacade: OrderFacade

    @MockkBean
    private lateinit var paymentFacade: PaymentFacade

    @MockkBean
    private lateinit var customerFacade: CustomerFacade

    @MockkBean
    private lateinit var catalogFacade: CatalogFacade

    private lateinit var allMocks: Array<Any>

    @BeforeEach
    fun setUp() {
        allMocks = arrayOf(inventoryFacade, orderFacade, paymentFacade, customerFacade, catalogFacade)
    }

    @Test
    fun `on OrderPlacedEvent`(scenario: Scenario) {
        val event = OrderPlacedEvent.example()

        var productsWereReserved = false
        every { inventoryFacade.reserveProducts(event.toProductsReservation()) } answers {
            productsWereReserved = true
        }

        scenario.publish(event)
            .andWaitForStateChange {
                productsWereReserved
            }

        verify { inventoryFacade.reserveProducts(event.toProductsReservation()) }

        confirmVerified(*allMocks)
    }

    @Test
    fun `on InventoryAllocationSuccessfulEvent`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val event = InventoryAllocationSuccessfulEvent(orderId)

        every { orderFacade.markStockAllocated(event.orderId) } just runs

        val paymentDetails = PaymentDetails(
            customerId = CustomerId.generate(),
            totalAmount = 42,
        )
        every { orderFacade.getPaymentDetails(event.orderId) } returns paymentDetails

        var paymentProcessed = false
        every {
            paymentFacade.processPayment(
                event.orderId,
                paymentDetails.customerId,
                paymentDetails.totalAmount,
            )
        } answers {
            paymentProcessed = true
        }

        scenario.publish(event)
            .andWaitForStateChange {
                paymentProcessed
            }

        verifySequence {
            orderFacade.markStockAllocated(event.orderId)
            orderFacade.getPaymentDetails(event.orderId)
            paymentFacade.processPayment(event.orderId, paymentDetails.customerId, paymentDetails.totalAmount)
        }

        confirmVerified(*allMocks)
    }

    @Test
    fun `on PaymentSuccessfulEvent`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val event = PaymentSuccessfulEvent(orderId)

        every { orderFacade.markOrderAsPaid(event.orderId) } just runs
        every { inventoryFacade.completeReservation(event.orderId) } just runs

        var releasedForShipping = false
        every { inventoryFacade.releaseForShipping(event.orderId) } answers {
            releasedForShipping = true
        }

        scenario.publish(event)
            .andWaitForStateChange {
                releasedForShipping
            }

        verifySequence {
            orderFacade.markOrderAsPaid(event.orderId)
            inventoryFacade.completeReservation(event.orderId)
            inventoryFacade.releaseForShipping(event.orderId)
        }

        confirmVerified(*allMocks)
    }

    @Test
    fun `on OrderShippedEvent`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val event = OrderShippedEvent(orderId, instantNowTruncated())

        every { orderFacade.completeOrder(event.orderId, event.shippedTime) } just runs

        val paymentDetails = PaymentDetails(CustomerId.generate(), 47)
        every { orderFacade.getPaymentDetails(event.orderId) } returns paymentDetails

        var stored = false
        every {
            customerFacade.storeCustomerOrderHistory(
                customerId = paymentDetails.customerId,
                orderId = event.orderId,
                totalPrice = paymentDetails.totalAmount,
                instant = event.shippedTime,
            )
        } answers {
            stored = true
        }
        scenario.publish(event)
            .andWaitForStateChange {
                stored
            }

        verifySequence {
            orderFacade.completeOrder(event.orderId, event.shippedTime)
            orderFacade.getPaymentDetails(event.orderId)
            customerFacade.storeCustomerOrderHistory(
                customerId = paymentDetails.customerId,
                orderId = event.orderId,
                totalPrice = paymentDetails.totalAmount,
                instant = event.shippedTime,
            )
        }

        confirmVerified(*allMocks)
    }

    @Test
    fun `on InventoryAllocationFailedEvent`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val event = InventoryAllocationFailedEvent(
            orderId,
            setOf(
                TooFewProducts(
                    productId = ProductId.generate(),
                    orderQuantity = 42,
                    inventoryQuantity = 6,
                ),
            ),
        )

        every { inventoryFacade.cancelReservation(event.orderId) } just runs

        var orderCancelled = false
        every { orderFacade.cancelOrder(event.orderId) } answers {
            orderCancelled = true
        }
        scenario.publish(event)
            .andWaitForStateChange {
                orderCancelled
            }

        verifySequence {
            inventoryFacade.cancelReservation(event.orderId)
            orderFacade.cancelOrder(event.orderId)
        }

        confirmVerified(*allMocks)
    }

    @Test
    fun `on PaymentFailedEvent`(scenario: Scenario) {
        val orderId = OrderId.generate()
        val event = PaymentFailedEvent(orderId, "Absolutely no money at all!")

        every { inventoryFacade.cancelReservation(event.orderId) } just runs

        var orderCancelled = false
        every { orderFacade.cancelOrder(event.orderId) } answers {
            orderCancelled = true
        }
        scenario.publish(event)
            .andWaitForStateChange {
                orderCancelled
            }

        verifySequence {
            inventoryFacade.cancelReservation(event.orderId)
            orderFacade.cancelOrder(event.orderId)
        }

        confirmVerified(*allMocks)
    }
}