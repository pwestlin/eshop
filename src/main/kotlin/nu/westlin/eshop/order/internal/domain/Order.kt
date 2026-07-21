package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
data class Order(
    @Id
    val id: OrderId,
    @Version
    val version: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
    val customerId: CustomerId,
    val status: OrderStatus,
    @MappedCollection(idColumn = "order_id")
    val items: OrderLineItems,
    val discount: Percentage,
    val subTotal: Int = items.subTotal,
    val grandTotal: Int = (subTotal * (1.0 - discount.fraction)).toInt(),
    val shippedTime: Instant? = null,
) {
    init {
        require(
            subTotal == items.subTotal,
        ) { "'subTotal' ($subTotal) is not equal to sub total om the items (${items.subTotal})" }
        require(
            subTotal == items.subTotal,
        ) {
            "'grandTotal' ($grandTotal) is not equal to sub total - 'discount' (${(subTotal * (1.0 - discount.fraction)).toInt()})"
        }

        if (status == OrderStatus.SHIPPED) {
            requireNotNull(shippedTime) {
                "Shipped time (shippedTime) must be provided when status is ${OrderStatus.SHIPPED}"
            }
        } else {
            require(shippedTime == null) {
                "Shipped time (shippedTime) cannot be set when status is $status"
            }
        }

        if (status == OrderStatus.PENDING) {
            require(createdAt == updatedAt) {
                "updatedAt must be equal to createdAt for status ${OrderStatus.PENDING}"
            }
        }
    }

    // Snygg domän-funktion för att byta status (skapar en kopia)
    fun ship(shippedTime: Instant): Order = this.copy(
        status = OrderStatus.SHIPPED,
        updatedAt = instantNowTruncated(),
        shippedTime = shippedTime,
    )

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
            this.status == OrderStatus.PENDING,
        ) {
            "Order with id $id must be in state ${OrderStatus.PENDING} but was in state $status"
        }

        return this.copy(status = OrderStatus.STOCKRESERVED, updatedAt = instantNowTruncated())
    }

    fun applyPaymentSuccessful(): Order {
        check(
            this.status == OrderStatus.STOCKRESERVED,
        ) {
            "Order with id $id must be in state ${OrderStatus.STOCKRESERVED} but was in state $status"
        }

        return this.copy(status = OrderStatus.PAID, updatedAt = instantNowTruncated())
    }

    fun cancel(): Order = this.copy(status = OrderStatus.CANCELLED, updatedAt = instantNowTruncated())

    companion object {
        fun new(id: OrderId, customerId: CustomerId, items: OrderLineItems, discount: Percentage): Order {
            val now = instantNowTruncated()
            return Order(
                id = id,
                createdAt = now,
                updatedAt = now,
                customerId = customerId,
                status = OrderStatus.PENDING,
                items = items,
                discount = discount,
            )
        }
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
    PENDING,
    STOCKRESERVED,
    PAID,
    SHIPPED,
    CANCELLED,
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