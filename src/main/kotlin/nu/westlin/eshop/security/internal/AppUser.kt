package nu.westlin.eshop.security.internal

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("app_user")
data class AppUser(
    @Id val username: String,
    val password: String,
    val roles: String, // Comma-separated roles, "Customer,CustomerManager"
)