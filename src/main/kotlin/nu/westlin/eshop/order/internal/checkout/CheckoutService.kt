package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderLineItem
import org.springframework.stereotype.Service

@Service
class CheckoutService(private val orderRepository: OrderRepository) {
    // TODO pwestlin: Returnera sumtyp för ev fel
    fun processCheckout(customerId: CustomerId, items: Set<OrderLineItem>, orderId: OrderId): Order {
        // TODO pwestlin: Kolla om OrderId redan finns (idempotens)

        val order = Order.new(
            id = orderId,
            customerId = customerId,
            items = items,
        )
        val createdOrder = orderRepository.insert(order)
        println("order = $order")
        println("createdOrder = $createdOrder")
        return order
    }
    // TODO pwestlin: Använd Repo
}