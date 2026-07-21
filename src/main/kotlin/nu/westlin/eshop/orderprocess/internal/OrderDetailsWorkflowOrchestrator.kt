package nu.westlin.eshop.orderprocess.internal

import nu.westlin.eshop.common.logger
import nu.westlin.eshop.customer.CustomerFacade
import nu.westlin.eshop.inventory.InventoryAllocationFailedEvent
import nu.westlin.eshop.inventory.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.inventory.InventoryFacade
import nu.westlin.eshop.inventory.OrderShippedEvent
import nu.westlin.eshop.inventory.ProductsReservation
import nu.westlin.eshop.order.OrderFacade
import nu.westlin.eshop.order.OrderPlacedEvent
import nu.westlin.eshop.payment.PaymentFacade
import nu.westlin.eshop.payment.PaymentFailedEvent
import nu.westlin.eshop.payment.PaymentSuccessfulEvent
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component

@Suppress("LoggingSimilarMessage")
@Component
class OrderDetailsWorkflowOrchestrator(
    private val inventoryFacade: InventoryFacade,
    private val orderFacade: OrderFacade,
    private val paymentFacade: PaymentFacade,
    private val customerFacade: CustomerFacade,
) {
    private val logger = logger()

    // TODO pwestlin: Vad göra om det kastas ett exception?
    //  I funktioner annoterade med @ApplicationModuleListener används Transactional Outbox Pattern vilket innebär att de ligger kvar.

    @ApplicationModuleListener
    fun on(event: OrderPlacedEvent) {
        logger.info("Order placed: ${event.orderId}")
        inventoryFacade.reserveProducts(event.toProductsReservation())
    }

    @ApplicationModuleListener
    fun on(event: InventoryAllocationSuccessfulEvent) {
        logger.info("Inventory allocation successful: ${event.orderId}")
        orderFacade.markStockAllocated(event.orderId)

        // Vi hämtar beloppet på ett säkert sätt utan att exponera hela entiteten
        val paymentDetails = orderFacade.getPaymentDetails(event.orderId)
        paymentFacade.processPayment(event.orderId, paymentDetails.customerId, paymentDetails.totalAmount)
    }

    @ApplicationModuleListener
    fun on(event: PaymentSuccessfulEvent) {
        logger.info("Payment successful: ${event.orderId}")
        orderFacade.markOrderAsPaid(event.orderId)
        inventoryFacade.completeReservation(event.orderId)
        inventoryFacade.releaseForShipping(event.orderId)
    }

    @ApplicationModuleListener
    fun on(event: OrderShippedEvent) {
        orderFacade.completeOrder(event.orderId, event.shippedTime)
        logger.info("Order shipped: ${event.orderId}")

        val details = orderFacade.getPaymentDetails(event.orderId)
        customerFacade.storeCustomerOrderHistory(
            customerId = details.customerId,
            orderId = event.orderId,
            grandTotal = details.totalAmount,
            instant = event.shippedTime,
        )
    }

    @ApplicationModuleListener
    fun on(event: InventoryAllocationFailedEvent) {
        logger.info("Order cancelled: $event")
        inventoryFacade.cancelReservation(event.orderId)
        orderFacade.cancelOrder(event.orderId)
    }

    @ApplicationModuleListener
    fun on(event: PaymentFailedEvent) {
        logger.info("Order cancelled: $event")
        inventoryFacade.cancelReservation(event.orderId)
        orderFacade.cancelOrder(event.orderId)
    }
}

fun OrderPlacedEvent.toProductsReservation(): ProductsReservation = ProductsReservation(
    orderId = orderId,
    items = items.map { item ->
        ProductsReservation.Item(
            productId = item.productId,
            quantity = item.quantity,
        )
    }.toSet(),
)