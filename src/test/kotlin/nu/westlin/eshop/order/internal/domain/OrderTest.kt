package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OrderTest {

    @Test
    fun `equals is overridden and only check id`() {
        val originalOrder = Order.example()
        assertThat(originalOrder).isEqualTo(originalOrder)

        assertThat(originalOrder.copy()).isEqualTo(originalOrder)

        assertThat(originalOrder.copy(id = OrderId.generate())).isNotEqualTo(originalOrder)

        assertThat(Order.example(id = originalOrder.id)).isEqualTo(originalOrder)

        assertThat(Order.example(id = originalOrder.id))
            .usingRecursiveComparison()
            .isNotEqualTo(originalOrder)
    }

    @Test
    fun `hashCode is overridden and only use id`() {
        val originalOrder = Order.example()
        val originalHashCode = originalOrder.hashCode()
        assertThat(originalOrder.hashCode()).isEqualTo(originalHashCode)

        assertThat(originalOrder.copy().hashCode()).isEqualTo(originalHashCode)

        assertThat(originalOrder.copy(id = OrderId.generate()).hashCode()).isNotEqualTo(originalHashCode)

        assertThat(Order.example(id = originalOrder.id).hashCode()).isEqualTo(originalHashCode)
    }

    @Test
    fun `new creates an order with status Pending`() {
        val orderId = OrderId.generate()
        val customerId = CustomerId.generate()
        val orderLineItems = OrderLineItems(List(3) { OrderLineItem.example() }.toSet())

        val order = Order.example(
            id = orderId,
            customerId = customerId,
            items = orderLineItems,
        )
        with(order) {
            assertThat(this.id).isEqualTo(orderId)
            assertThat(this.customerId).isEqualTo(customerId)
            assertThat(this.items).isEqualTo(orderLineItems)
            assertThat(this.status).isEqualTo(OrderStatus.Pending)
        }
    }

    @Test
    fun `ship changes status to Shipped`() {
        val order = Order.example(status = OrderStatus.Pending)
        val shippedTime = instantNowTruncated()
        assertThat(order.ship(shippedTime))
            .usingRecursiveComparison()
            .isEqualTo(order.copy(status = OrderStatus.Shipped, shippedTime = shippedTime))
    }

    @Test
    fun `applyInventoryAllocationSuccessful changes status to StockReserved`() {
        val order = Order.example(status = OrderStatus.Pending)
        assertThat(order.applyInventoryAllocationSuccessful())
            .usingRecursiveComparison()
            .isEqualTo(order.copy(status = OrderStatus.StockReserved))
    }

    @Test
    fun `applyInventoryAllocationSuccessful throws IllegalStateException when order has the wrong status`() {
        OrderStatus.entries.filter { it != OrderStatus.Pending }.forEach { orderStatus ->
            val shippedTime = if (orderStatus == OrderStatus.Shipped) {
                instantNowTruncated()
            } else {
                null
            }
            val order = Order.example(status = orderStatus, shippedTime = shippedTime)
            assertThatThrownBy { order.applyInventoryAllocationSuccessful() }
                .isExactlyInstanceOf<IllegalStateException>()
                .hasMessage(
                    "Order with id ${order.id} must be in state ${OrderStatus.Pending} but was in state $orderStatus",
                )
        }
    }

    @Test
    fun `applyPaymentSuccessful changes status to StockReserved`() {
        val order = Order.example(status = OrderStatus.StockReserved)
        assertThat(order.applyPaymentSuccessful())
            .usingRecursiveComparison()
            .isEqualTo(order.copy(status = OrderStatus.Paid))
    }

    @Test
    fun `applyPaymentSuccessful throws IllegalStateException when order has the wrong status`() {
        OrderStatus.entries.filter { it != OrderStatus.StockReserved }.forEach { orderStatus ->
            val shippedTime = if (orderStatus == OrderStatus.Shipped) {
                instantNowTruncated()
            } else {
                null
            }
            val order = Order.example(status = orderStatus, shippedTime = shippedTime)
            assertThatThrownBy { order.applyPaymentSuccessful() }
                .isExactlyInstanceOf<IllegalStateException>()
                .hasMessage(
                    "Order with id ${order.id} must be in state ${OrderStatus.StockReserved} but was in state $orderStatus",
                )
        }
    }

    @Test
    fun `cancel changes status to Cancelled`() {
        val order = Order.example(status = OrderStatus.Pending)
        assertThat(order.cancel())
            .usingRecursiveComparison()
            .isEqualTo(order.copy(status = OrderStatus.Cancelled))
    }

    @Test
    fun `subTotal should be the sum of all OrderLineItems`() {
        val order = Order.example(
            items = OrderLineItems(
                setOf(
                    OrderLineItem.example(price = 1, quantity = 5),
                    OrderLineItem.example(price = 3, quantity = 3),
                    OrderLineItem.example(price = 5, quantity = 1),
                ),
            ),
        )
        assertThat(order.subTotal).isEqualTo(19)
    }

    @Test
    fun `shippedTime must be provided when status is Shipped`() {
        assertThatThrownBy { Order.example(status = OrderStatus.Shipped) }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Shipped time (shippedTime) must be provided when status is Shipped")
    }
}