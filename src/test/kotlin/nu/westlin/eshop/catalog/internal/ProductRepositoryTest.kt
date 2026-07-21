package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.CurrencySpringDataJdbcConfiguration
import nu.westlin.eshop.config.ProductSpringDataJdbcConfiguration
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.dao.DuplicateKeyException

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
    SharedTestcontainersConfiguration::class,
    ProductRepository::class,
    ProductSpringDataJdbcConfiguration::class,
    CurrencySpringDataJdbcConfiguration::class,
)
class ProductRepositoryTest @Autowired constructor(private val repository: ProductRepository) {

    @Test
    fun `insert new product`() {
        val product = Product.example()
        repository.insert(product)

        assertThat(repository.exists(product.id)).isTrue
    }

    @Test
    fun `insert a product with a key that already exists`() {
        val product = Product.example()
        repository.insert(Product.example(id = product.id))

        assertThatThrownBy { repository.insert(product) }
            .isExactlyInstanceOf<DuplicateKeyException>()
            .hasMessageContaining("duplicate key value violates unique constraint")
    }
}