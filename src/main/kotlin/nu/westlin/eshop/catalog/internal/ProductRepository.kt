package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataProductRepository : ListCrudRepository<Product, ProductId>

@Repository
class ProductRepository(
    private val springDataRepository: SpringDataProductRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(product: Product) {
        entityTemplate.insert(product)
    }

    fun exists(id: ProductId): Boolean = springDataRepository.existsById(id)

    fun findAll(): List<Product> = springDataRepository.findAll()
}