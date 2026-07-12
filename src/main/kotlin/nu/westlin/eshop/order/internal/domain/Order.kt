package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Id
    val id: OrderId,
    val customerId: CustomerId,
    val status: OrderStatus,
    // TODO pwestlin: Får inte vara tom. "OrderLineItems"?
    @MappedCollection(idColumn = "order_id")
    val items: Set<OrderLineItem>,
) {

    // Snygg domän-funktion för att byta status (skapar en kopia)
    fun ship(): Order = this.copy(status = OrderStatus.SHIPPED)

    companion object {
        fun new(id: OrderId, customerId: CustomerId, items: Set<OrderLineItem>): Order = Order(
            id = id,
            customerId = customerId,
            status = OrderStatus.PENDING,
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
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    CANCELLED,
}
