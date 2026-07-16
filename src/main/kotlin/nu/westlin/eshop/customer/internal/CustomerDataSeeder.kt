package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.deleteAll
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Profile("testdata")
@Component
class CustomerDataSeeder(private val entityTemplate: JdbcAggregateTemplate) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        entityTemplate.deleteAll<Customer>()

        entityTemplate.insert(
            Customer(
                id = CustomerId(UUID.fromString("d9538d50-8976-4dea-9b5f-96ecf5bbafc5")),
                name = "Sune Son",
                email = Email("sune@son.nu"),
            ),
        )

        entityTemplate.insert(
            Customer(
                id = CustomerId(UUID.fromString("31eea1b4-f80b-43f6-9a6a-571430c88cd1")),
                name = "Koma Klasse",
                email = Email("koma@klasse.nu"),
            ),
        )
    }
}
