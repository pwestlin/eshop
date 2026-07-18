package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.InventoryAllocationFailedEvent
import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderCancelledEvent
import nu.westlin.eshop.common.OrderCompletedEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderShippedEvent
import nu.westlin.eshop.common.PaymentFailedEvent
import nu.westlin.eshop.common.PaymentSuccessfulEvent
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class OrderStatusChangesService(
    private val orderRepository: OrderRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @ApplicationModuleListener
    fun handleInventoryAllocationSuccessfulEvent(event: InventoryAllocationSuccessfulEvent) {
        val order = getOrder(event.orderId)
        orderRepository.update(order.applyInventoryAllocationSuccessful())
    }

    // TODO pwestlin: testa
    @ApplicationModuleListener
    fun handleInventoryAllocationFailedEvent(event: InventoryAllocationFailedEvent) {
        val order = getOrder(event.orderId)
        check(
            order.status == OrderStatus.Pending,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.Pending} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.cancel())
        eventPublisher.publishEvent(OrderCancelledEvent(event.orderId))
    }

    @ApplicationModuleListener
    fun handlePaymentSuccessfulEvent(event: PaymentSuccessfulEvent) {
        val order = getOrder(event.orderId)
        orderRepository.update(order.applyPaymentSuccessful())
    }

    // TODO pwestlin: testa
    @ApplicationModuleListener
    fun handlePaymentFailedEvent(event: PaymentFailedEvent) {
        val order = getOrder(event.orderId)
        check(
            order.status == OrderStatus.StockReserved,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.StockReserved} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.cancel())
        eventPublisher.publishEvent(OrderCancelledEvent(event.orderId))
    }

    @ApplicationModuleListener
    fun handleOrderShippedEvent(event: OrderShippedEvent) {
        val order = getOrder(event.orderId)
        orderRepository.update(order.ship(event.shippedTime))
        eventPublisher.publishEvent(
            OrderCompletedEvent(
                orderId = order.id,
                customerId = order.customerId,
                totalPrice = order.totalPrice,
                occurredAt = event.shippedTime,
            ),
        )
    }

    private fun getOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId)
        checkNotNull(order) { "Order with id $orderId does not exist" }
        return order
    }
}