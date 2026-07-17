package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.customer.Percentage
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
data class Order(
    @Id
    val id: OrderId,
    val createdAt: Instant,
    val customerId: CustomerId,
    val status: OrderStatus,
    @MappedCollection(idColumn = "order_id")
    val items: OrderLineItems,
    val discount: Percentage,
    val subTotal: Int = items.subTotal,
    val totalPrice: Int = (subTotal * (1.0 - discount.fraction)).toInt(),
    val shippedTime: Instant? = null,
) {
    init {
        require(
            subTotal == items.subTotal,
        ) { "'subTotal' ($subTotal) is not equal to sub total om the items (${items.subTotal})" }
        require(
            subTotal == items.subTotal,
        ) {
            "'totalPrice' ($totalPrice) is not equal to sub total - 'discount' (${(subTotal * (1.0 - discount.fraction)).toInt()})"
        }

        if (status == OrderStatus.Shipped) {
            requireNotNull(shippedTime) {
                "Shipped time (shippedTime) must be provided when status is ${OrderStatus.Shipped}"
            }
        } else {
            require(shippedTime == null) {
                "Shipped time (shippedTime) cannot be set when status is $status"
            }
        }
    }

    // Snygg domän-funktion för att byta status (skapar en kopia)
    fun ship(shippedTime: Instant): Order = this.copy(status = OrderStatus.Shipped, shippedTime = shippedTime)

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    fun applyInventoryAllocationSuccessful(): Order {
        check(
            this.status == OrderStatus.Pending,
        ) {
            "Order with id $id must be in state ${OrderStatus.Pending} but was in state $status"
        }

        return this.copy(status = OrderStatus.StockReserved)
    }

    fun applyPaymentSuccessful(): Order {
        check(
            this.status == OrderStatus.StockReserved,
        ) {
            "Order with id $id must be in state ${OrderStatus.StockReserved} but was in state $status"
        }

        return this.copy(status = OrderStatus.Paid)
    }

    fun cancel(): Order = this.copy(status = OrderStatus.Cancelled)

    companion object {
        fun new(id: OrderId, customerId: CustomerId, items: OrderLineItems, discount: Percentage): Order = Order(
            id = id,
            createdAt = instantNowTruncated(),
            customerId = customerId,
            status = OrderStatus.Pending,
            items = items,
            discount = discount,
        )
    }
}

@Table("order_line_items")
data class OrderLineItem(
    @Id
    val id: Long? = null,
    val productId: ProductId,
    val quantity: Int,
    val price: Int,
) {
    companion object
}

enum class OrderStatus {
    Pending,
    StockReserved,
    Paid,
    Shipped,
    Cancelled,
}

@JvmInline
value class OrderLineItems(val value: Set<OrderLineItem>) {
    init {
        require(value.isNotEmpty()) { "value can't be empty" }
    }

    val subTotal: Int
        get() = value.sumOf { it.price * it.quantity }

    companion object
}