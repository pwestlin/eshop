package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("inventory_items")
data class InventoryItem(
    @Id
    val productId: ProductId,
    @Version
    val version: Int = 0,
    val quantity: Int,
) {

    init {
        require(quantity >= 0) { "quantity must be >= 0 but was $quantity" }
    }

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryItem

        return productId == other.productId
    }

    override fun hashCode(): Int = productId.hashCode()

    companion object
}
