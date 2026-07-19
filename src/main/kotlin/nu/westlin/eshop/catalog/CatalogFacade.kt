package nu.westlin.eshop.catalog

import nu.westlin.eshop.catalog.internal.ProductRepository
import nu.westlin.eshop.common.ProductId
import org.springframework.stereotype.Component

@Component
class CatalogFacade(private val productRepository: ProductRepository) {

    fun exists(id: ProductId): Boolean = productRepository.exists(id)

    fun allProductsExist(productIds: List<ProductId>): AllProductsExistResponse {
        val notExistingProductIds: List<ProductId> = productIds.filterNot { productRepository.exists(it) }
        return if(notExistingProductIds.isEmpty()) {
            AllProductsExistResponse.AllExist
        } else {
            AllProductsExistResponse.MissingProducts(notExistingProductIds)
        }
    }
}

sealed interface AllProductsExistResponse {

    data object AllExist : AllProductsExistResponse
    data class MissingProducts(val productIds: List<ProductId>) : AllProductsExistResponse
}