package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.InventoryAllocationFailedEvent
import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.PaymentFailedEvent
import nu.westlin.eshop.common.PaymentSuccessfulEvent
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderStatus
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class OrderStatusChangesService(private val orderRepository: OrderRepository) {

    @ApplicationModuleListener
    fun handleInventoryAllocationSuccessfulEvent(event: InventoryAllocationSuccessfulEvent) {
        val order = getOrder(event.orderId)
        orderRepository.update(order.applyInventoryAllocationSuccessful())
    }

    @ApplicationModuleListener
    fun handleInventoryAllocationFailedEvent(event: InventoryAllocationFailedEvent) {
        val order = getOrder(event.orderId)
        check(
            order.status == OrderStatus.Pending,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.Pending} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.cancel())
    }

    @ApplicationModuleListener
    fun handlePaymentSuccessfulEvent(event: PaymentSuccessfulEvent) {
        val order = getOrder(event.orderId)
        orderRepository.update(order.applyPaymentSuccessful())
    }

    @ApplicationModuleListener
    fun handlePaymentFailedEvent(event: PaymentFailedEvent) {
        val order = getOrder(event.orderId)
        check(
            order.status == OrderStatus.StockReserved,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.StockReserved} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.cancel())
    }

    private fun getOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId)
        checkNotNull(order) { "Order with id $orderId does not exist" }
        return order
    }
}