package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.deleteAll
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Suppress("MagicNumber")
@Profile("testdata")
@Component
class CatalogDataSeeder(private val entityTemplate: JdbcAggregateTemplate) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        entityTemplate.deleteAll<Product>()

        entityTemplate.insert(
            Product(
                id = ProductId(1),
                name = "Muffler",
                description = "A really nice muffler!",
                price = 5,

                ),
        )

        entityTemplate.insert(
            Product(
                id = ProductId(2),
                name = "Brake caliper",
                description = "A really nice brake caliper!",
                price = 42,

                ),
        )

        entityTemplate.insert(
            Product(
                id = ProductId(3),
                name = "Tyre",
                description = "'A really nice tyre!",
                price = 69,
            ),
        )
    }
}