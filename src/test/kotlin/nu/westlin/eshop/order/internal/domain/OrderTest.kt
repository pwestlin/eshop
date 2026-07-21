package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderTest {

    @Test
    fun `subTotal must match total of items (items_subTotal)`() {
        val order = Order.example()
        val newSubTotal = order.subTotal + Money.sek(5)
        assertThatThrownBy {
            @Suppress("UnusedDataClassCopyResult")
            order.copy(subTotal = newSubTotal)
        }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("'subTotal' ($newSubTotal) does not match total of items (${order.items.subTotal})")
    }

    @Test
    fun `grandTotal must match subTotal minus discount`() {
        val order = Order.example()
        val newGrandTotal = order.grandTotal - Money.sek(5)
        assertThatThrownBy {
            @Suppress("UnusedDataClassCopyResult")
            order.copy(grandTotal = newGrandTotal)
        }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage(
                "'grandTotal' ($newGrandTotal) is not equal to sub total after discount (${order.subTotal - (order.subTotal * order.discount)})",
            )
    }

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
    fun `new creates an order with status PENDING`() {
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
            assertThat(this.status).isEqualTo(OrderStatus.PENDING)
        }
    }

    @Test
    fun `ship changes status to SHIPPED`() {
        val order = Order.example(status = OrderStatus.PENDING)
        val shippedTime = instantNowTruncated()
        assertThat(order.ship(shippedTime))
            .usingRecursiveComparison()
            .ignoringFields("updatedAt")
            .isEqualTo(order.copy(status = OrderStatus.SHIPPED, shippedTime = shippedTime))
    }

    @Test
    fun `applyInventoryAllocationSuccessful changes status to STOCK_RESERVED`() {
        val order = Order.example(status = OrderStatus.PENDING)
        assertThat(order.applyInventoryAllocationSuccessful())
            .usingRecursiveComparison()
            .ignoringFields("updatedAt")
            .isEqualTo(order.copy(status = OrderStatus.STOCK_RESERVED))
    }

    @Test
    fun `applyInventoryAllocationSuccessful throws IllegalStateException when order has the wrong status`() {
        OrderStatus.entries.filter { it != OrderStatus.PENDING }.forEach { orderStatus ->
            val shippedTime = if (orderStatus == OrderStatus.SHIPPED) {
                instantNowTruncated()
            } else {
                null
            }
            val order = Order.example(status = orderStatus, shippedTime = shippedTime)
            assertThatThrownBy { order.applyInventoryAllocationSuccessful() }
                .isExactlyInstanceOf<IllegalStateException>()
                .hasMessage(
                    "Order with id ${order.id} must be in state ${OrderStatus.PENDING} but was in state $orderStatus",
                )
        }
    }

    @Test
    fun `applyPaymentSuccessful changes status to STOCKRESERVED`() {
        val order = Order.example(status = OrderStatus.STOCK_RESERVED)
        assertThat(order.applyPaymentSuccessful())
            .usingRecursiveComparison()
            .ignoringFields("updatedAt")
            .isEqualTo(order.copy(status = OrderStatus.PAID))
    }

    @Test
    fun `applyPaymentSuccessful throws IllegalStateException when order has the wrong status`() {
        OrderStatus.entries.filter { it != OrderStatus.STOCK_RESERVED }.forEach { orderStatus ->
            val shippedTime = if (orderStatus == OrderStatus.SHIPPED) {
                instantNowTruncated()
            } else {
                null
            }
            val order = Order.example(status = orderStatus, shippedTime = shippedTime)
            assertThatThrownBy { order.applyPaymentSuccessful() }
                .isExactlyInstanceOf<IllegalStateException>()
                .hasMessage(
                    "Order with id ${order.id} must be in state ${OrderStatus.STOCK_RESERVED} but was in state $orderStatus",
                )
        }
    }

    @Test
    fun `cancel changes status to CANCELLED`() {
        val order = Order.example(status = OrderStatus.PENDING)
        assertThat(order.cancel())
            .usingRecursiveComparison()
            .ignoringFields("updatedAt")
            .isEqualTo(order.copy(status = OrderStatus.CANCELLED))
    }

    @Test
    fun `fail changes status to FAILED`() {
        val order = Order.example(status = OrderStatus.PENDING)
        assertThat(order.fail())
            .usingRecursiveComparison()
            .ignoringFields("updatedAt")
            .isEqualTo(order.copy(status = OrderStatus.FAILED))
    }

    @Test
    fun `subTotal should be the sum of all OrderLineItems`() {
        val order = Order.example(
            items = OrderLineItems(
                setOf(
                    OrderLineItem.example(price = Money.sek(1), quantity = 5),
                    OrderLineItem.example(price = Money.sek(3), quantity = 3),
                    OrderLineItem.example(price = Money.sek(5), quantity = 1),
                ),
            ),
        )
        assertThat(order.subTotal).isEqualTo(Money.sek(19))
    }

    @Test
    fun `shippedTime must be provided when status is Shipped`() {
        assertThatThrownBy { Order.example(status = OrderStatus.SHIPPED) }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Shipped time (shippedTime) must be provided when status is SHIPPED")
    }

    @DisplayName("grandTotal")
    @Nested
    inner class OrderGrandTotalTest {

        @Test
        fun `0 percent discount`() {
            val order = Order.example(
                items = OrderLineItems.example(
                    listOf(
                        OrderLineItem.example(quantity = 2, price = Money.sek(100)),
                        OrderLineItem.example(quantity = 4, price = Money.sek(200)),
                    ).toSet(),
                ),
                discount = Percentage.ZERO,
            )
            assertThat(order.subTotal).isEqualTo(Money.sek(1_000))
            assertThat(order.grandTotal).isEqualTo(Money.sek(1_000))
        }

        @Test
        fun `10 percent discount`() {
            val order = Order.example(
                items = OrderLineItems.example(
                    listOf(
                        OrderLineItem.example(quantity = 2, price = Money.sek(100)),
                        OrderLineItem.example(quantity = 4, price = Money.sek(200)),
                    ).toSet(),
                ),
                discount = Percentage(0.1),
            )
            assertThat(order.subTotal).isEqualTo(Money.sek(1_000))
            assertThat(order.grandTotal).isEqualTo(Money.sek(900))
        }

        @Test
        fun `70 percent discount`() {
            val order = Order.example(
                items = OrderLineItems.example(
                    listOf(
                        OrderLineItem.example(quantity = 2, price = Money.sek(100)),
                        OrderLineItem.example(quantity = 4, price = Money.sek(200)),
                    ).toSet(),
                ),
                discount = Percentage(0.7),
            )
            assertThat(order.subTotal).isEqualTo(Money.sek(1_000))
            assertThat(order.grandTotal).isEqualTo(Money.sek(300))
        }

        @Test
        fun `15_3 percent discount`() {
            val order = Order.example(
                items = OrderLineItems.example(
                    listOf(
                        OrderLineItem.example(quantity = 2, price = Money.sek(100)),
                        OrderLineItem.example(quantity = 4, price = Money.sek(200)),
                    ).toSet(),
                ),
                discount = Percentage(0.153),
            )
            assertThat(order.subTotal).isEqualTo(Money.sek(1_000))
            assertThat(order.grandTotal).isEqualTo(Money.sek(BigDecimal.valueOf(84_700, 2)))
        }
    }
}