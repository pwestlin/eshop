package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderLineItem
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class CheckoutService(
    private val orderRepository: OrderRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    // TODO pwestlin: Returnera sumtyp för ev fel?
    @Transactional
    fun processCheckout(customerId: CustomerId, items: Set<OrderLineItem>, orderId: OrderId): Order {
        // TODO pwestlin: Kolla om OrderId redan finns (idempotens). Denna kontroll ska göras i controllern!

        val order = Order.new(
            id = orderId,
            customerId = customerId,
            items = items,
        )
        val createdOrder = orderRepository.insert(order)
        println("order = $order")
        println("createdOrder = $createdOrder")
        eventPublisher.publishEvent(
            OrderPlacedEvent(
                orderId = order.id,
                customerId = order.customerId,
                items = order.items.map { item ->
                    OrderPlacedEvent.OrderPlacedItem(
                        productId = item.productId,
                        quantity = item.quantity,

                    )
                }.toSet(),
                occurredAt = Instant.now().truncatedTo(ChronoUnit.MICROS),
            ),
        )
        return createdOrder
    }
}