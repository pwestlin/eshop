package nu.westlin.eshop.catalog

import nu.westlin.eshop.catalog.internal.ProductRepository
import nu.westlin.eshop.common.ProductId
import org.springframework.stereotype.Service

@Service
class CatalogService(private val productRepository: ProductRepository) {

    fun exists(id: ProductId): Boolean = productRepository.exists(id)
}