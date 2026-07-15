package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("customers")
data class Customer(
    @Id
    val id: CustomerId,
    val name: String,
    val email: Email,
) {

    // equals() and hashCode() are overridden because Order is an entity and not a value object (as of DDD).
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Customer

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object
}

@JvmInline
value class Email(val value: String) {

    init {
        require(emailRegex.matches(value)) { "value '$value' does not match regex $emailRegex" }
    }

    companion object {
        val emailRegex: Regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
