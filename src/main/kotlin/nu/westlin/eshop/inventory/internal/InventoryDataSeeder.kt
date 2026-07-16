package nu.westlin.eshop.inventory.internal

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
class InventoryDataSeeder(private val entityTemplate: JdbcAggregateTemplate) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String) {
        entityTemplate.deleteAll<InventoryItem>()

        entityTemplate.insert(
            InventoryItem(
                productId = ProductId(1),
                quantity = 50,
            ),
        )

        entityTemplate.insert(
            InventoryItem(
                productId = ProductId(2),
                quantity = 30,
            ),
        )

        entityTemplate.insert(
            InventoryItem(
                productId = ProductId(3),
                quantity = 69,
            ),
        )
    }
}