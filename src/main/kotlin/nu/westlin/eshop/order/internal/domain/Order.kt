package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    // TODO pwestlin: Generera order id från en sekvens?
    @Id
    val id: OrderId,
    val customerId: CustomerId,
    val status: OrderStatus,
    @MappedCollection(idColumn = "order_id")
    val items: OrderLineItems,
) {

    val subTotal: Int
        get() = items.subTotal

    // Snygg domän-funktion för att byta status (skapar en kopia)
    fun ship(): Order = this.copy(status = OrderStatus.Shipped)

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun new(id: OrderId, customerId: CustomerId, items: OrderLineItems): Order = Order(
            id = id,
            customerId = customerId,
            status = OrderStatus.Pending,
            items = items,
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
    Processing,
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