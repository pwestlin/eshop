package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.config.ProductSpringDataJdbcConfiguration
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.findById

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
    SharedTestcontainersConfiguration::class,
    ReservedInventoryItemRepository::class,
    ProductSpringDataJdbcConfiguration::class,
)
class ReservedInventoryItemRepositoryTest @Autowired constructor(
    private val repository: ReservedInventoryItemRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {

    @Test
    fun `insert should store in database`() {
        val item = ReservedInventoryItem.example()
        repository.insert(item)
        assertThat(entityTemplate.findById<ReservedInventoryItem>(item.id)).isEqualTo(item)
    }

    @Test
    fun `findByOrderId should find all items for an order`() {
        val orderId = OrderId.generate()

        val item1 = ReservedInventoryItem.example(ReservedInventoryItemId.example(orderId = orderId))
        repository.insert(item1)

        val item2 = ReservedInventoryItem.example(ReservedInventoryItemId.example(orderId = orderId))
        repository.insert(item2)

        repeat(5) {
            repository.insert(ReservedInventoryItem.example())
        }

        assertThat(repository.findByOrderId(orderId)).containsExactlyInAnyOrder(item1, item2)
    }

    @Test
    fun `findByProductId should find all items for several orders`() {
        repeat(5) {
            repository.insert(ReservedInventoryItem.example())
        }

        val productId = ProductId.generate()

        val item1 = ReservedInventoryItem.example(ReservedInventoryItemId.example(productId = productId))
        repository.insert(item1)

        val item2 = ReservedInventoryItem.example(ReservedInventoryItemId.example(productId = productId))
        repository.insert(item2)

        assertThat(repository.findByProductId(productId)).containsExactlyInAnyOrder(item1, item2)
    }

    @Test
    fun `delete should delete from database`() {
        val item = ReservedInventoryItem.example()
        repository.insert(item)
        assertThat(entityTemplate.findById<ReservedInventoryItem>(item.id)).isEqualTo(item)

        repository.delete(item)
        assertThat(entityTemplate.findById<ReservedInventoryItem>(item.id)).isNull()
    }
}