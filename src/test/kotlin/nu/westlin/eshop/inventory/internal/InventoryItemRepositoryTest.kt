package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.CurrencySpringDataJdbcConfiguration
import nu.westlin.eshop.config.ProductSpringDataJdbcConfiguration
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
    SharedTestcontainersConfiguration::class,
    InventoryItemRepository::class,
    ProductSpringDataJdbcConfiguration::class,
    CurrencySpringDataJdbcConfiguration::class,
)
class InventoryItemRepositoryTest @Autowired constructor(private val repository: InventoryItemRepository) {

    @Test
    fun `insert should be found by findById`() {
        val item = InventoryItem.example()
        val inserted = repository.insert(item)
        assertThat(inserted).isEqualTo(item.copy(version = inserted.version))
        assertThat(repository.getById(item.productId)).isEqualTo(item.copy(version = inserted.version))
    }

    @Test
    fun `update should update quantity`() {
        val item = InventoryItem.example()
        val inserted = repository.insert(item)
        assertThat(repository.getById(item.productId)).isEqualTo(item.copy(version = inserted.version))

        val updatedItem = inserted.copy(quantity = inserted.quantity + 1)
        repository.update(updatedItem)
        assertThat(repository.getById(item.productId)).isEqualTo(updatedItem.copy(version = inserted.version + 1))
    }
}