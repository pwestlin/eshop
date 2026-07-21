package nu.westlin.eshop.catalog

import nu.westlin.eshop.catalog.internal.Product
import nu.westlin.eshop.catalog.internal.SpringDataProductRepository
import nu.westlin.eshop.catalog.internal.example
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.config.ProductSpringDataJdbcConfiguration
import nu.westlin.eshop.test.ModulithIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.modulith.test.Scenario

@ModulithIntegrationTest
@Import(ProductSpringDataJdbcConfiguration::class)
class CatalogModuleIntegrationTest @Autowired constructor(
    private val catalogFacade: CatalogFacade,
    private val springDataProductRepository: SpringDataProductRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {

    @Test
    fun `create product`(scenario: Scenario) {
        val command = CreateProductCommand(
            productId = ProductId.generate(),
            name = "Foo",
            description = "Descr",
            price = 43,
        )

        scenario.stimulate { catalogFacade.createProduct(command) }
            .andWaitForStateChange { springDataProductRepository.findById(command.productId).orElse(null) }
            .andVerify { product ->
                requireNotNull(product)
                assertThat(product.id).isEqualTo(command.productId)
                assertThat(product.name).isEqualTo(command.name)
                assertThat(product.description).isEqualTo(command.description)
                assertThat(product.price).isEqualTo(command.price)
            }
    }

    @Test
    fun `exist - false`() {
        assertThat(catalogFacade.exists(ProductId.generate())).isFalse
    }

    @Test
    fun `exist - true`() {
        val product = Product.example()
        entityTemplate.insert(product)
        assertThat(catalogFacade.exists(product.id)).isTrue
    }

    @Test
    fun `allProductsExist - true`() {
        val product1 = Product.example().also {
            entityTemplate.insert(it)
        }
        val product2 = Product.example().also {
            entityTemplate.insert(it)
        }
        assertThat(
            catalogFacade.allProductsExist(listOf(product1.id, product2.id)),
        ).isEqualTo(AllProductsExistResponse.AllExist)
    }

    @Test
    fun `allProductsExist - false`() {
        val product1 = Product.example().also {
            entityTemplate.insert(it)
        }
        val product2 = Product.example().also {
            entityTemplate.insert(it)
        }
        val existingProductIds = listOf(product1.id, product2.id)

        val notExistingProductIds = List(3) { ProductId.generate() }

        assertThat(catalogFacade.allProductsExist(existingProductIds + notExistingProductIds))
            .isEqualTo(AllProductsExistResponse.MissingProducts(notExistingProductIds))
    }
}
