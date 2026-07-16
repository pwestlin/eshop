package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.InventoryAllocationFailedEvent
import nu.westlin.eshop.common.InventoryAllocationSuccessfulEvent
import nu.westlin.eshop.order.internal.domain.OrderStatus
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class OrderStateChangesService(private val orderRepository: OrderRepository) {

    @ApplicationModuleListener
    fun handleInventoryAllocationSuccessfulEvent(event: InventoryAllocationSuccessfulEvent) {
        val order = orderRepository.findById(event.orderId)
        checkNotNull(order) { "Order with id ${event.orderId} does not exist" }
        check(
            order.status == OrderStatus.Pending,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.Pending} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.applyInventoryAllocationSuccessful())
    }

    @ApplicationModuleListener
    fun handleInventoryAllocationFailedEvent(event: InventoryAllocationFailedEvent) {
        val order = orderRepository.findById(event.orderId)
        checkNotNull(order) { "Order with id ${event.orderId} does not exist" }
        check(
            order.status == OrderStatus.Pending,
        ) {
            "Order with id ${event.orderId} must be in state ${OrderStatus.Pending} when handling event $event but was in state ${order.status}"
        }
        orderRepository.update(order.copy(status = OrderStatus.Cancelled))
    }
}