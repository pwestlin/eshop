package nu.westlin.eshop.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.order.internal.checkout.OrderRepository
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderLineItem
import nu.westlin.eshop.order.internal.domain.OrderLineItems
import nu.westlin.eshop.order.internal.domain.OrderStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class OrderFacade(private val orderRepository: OrderRepository) {
    fun isOrderIdUnique(orderId: OrderId): Boolean = !orderRepository.exists(orderId)

    @Transactional
    fun createOrder(command: OrderCreationCommand) {
        val order = Order.new(
            id = command.orderId,
            customerId = command.customerId,
            items = OrderLineItems(
                command.items.map { item ->
                    OrderLineItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        price = item.price,
                    )
                }.toSet(),
            ),
            discount = Percentage(command.discount.value / 100.0),
        )
        orderRepository.insert(order)
    }

    @Transactional
    fun markStockAllocated(orderId: OrderId) {
        val order = getOrder(orderId)
        orderRepository.update(order.applyInventoryAllocationSuccessful())
    }

    fun getPaymentDetails(orderId: OrderId): PaymentDetails {
        val order = getOrder(orderId)
        return PaymentDetails(
            customerId = order.customerId,
            totalAmount = order.grandTotal,
        )
    }

    @Transactional
    fun markOrderAsPaid(orderId: OrderId) {
        val order = getOrder(orderId)
        orderRepository.update(order.applyPaymentSuccessful())
    }

    @Transactional
    fun completeOrder(orderId: OrderId, shippedTime: Instant) {
        val order = getOrder(orderId)
        orderRepository.update(order.ship(shippedTime))
    }

    @Transactional
    fun cancelOrder(orderId: OrderId) {
        val order = getOrder(orderId)
        check(
            order.status != OrderStatus.SHIPPED,
        ) {
            "Order with id $orderId must not be in state ${OrderStatus.SHIPPED} to get cancelled but was in state ${order.status}"
        }
        orderRepository.update(order.cancel())
    }

    private fun getOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId)
        checkNotNull(order) { "Order with id $orderId does not exist" }
        return order
    }
}

data class OrderCreationCommand(
    val orderId: OrderId,
    val customerId: CustomerId,
    val items: Set<Item>,
    val discount: OrderDiscountInput,
) {

    data class Item(val productId: ProductId, val quantity: Int, val price: Money) {
        companion object
    }

    companion object
}
