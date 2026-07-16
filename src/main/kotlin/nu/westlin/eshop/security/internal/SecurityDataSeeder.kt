package nu.westlin.eshop.security.internal

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.deleteAll
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("testdata")
@Component
class SecurityDataSeeder(private val entityTemplate: JdbcAggregateTemplate) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        entityTemplate.deleteAll<AppUser>()

        entityTemplate.insert(
            AppUser(
                username = "peter",
                password = "peter",
                roles = "CUSTOMER,ADMIN",
            ),
        )

        entityTemplate.insert(
            AppUser(
                username = "customer",
                password = "customer",
                roles = "CUSTOMER",
            ),
        )

        entityTemplate.insert(
            AppUser(
                username = "admin",
                password = "admin",
                roles = "ADMIN",
            ),
        )
    }
}