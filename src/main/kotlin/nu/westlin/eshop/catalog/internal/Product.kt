package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("products")
data class Product(
    @Id
    val id: ProductId,
    val name: String,
    val description: String,
) {

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object
}
