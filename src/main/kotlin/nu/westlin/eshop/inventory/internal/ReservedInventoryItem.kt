package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

data class ReservedInventoryItemId(val productId: ProductId, val orderId: OrderId) {
    companion object
}

@Table("reserved_inventory_items")
data class ReservedInventoryItem(
    @Id
    val id: ReservedInventoryItemId,
    val quantity: Int,
) {

    init {
        require(quantity >= 0) { "quantity must be >= 0 but was $quantity" }
    }

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReservedInventoryItem

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object
}
