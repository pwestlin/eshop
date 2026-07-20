package nu.westlin.eshop.catalog

import nu.westlin.eshop.catalog.internal.Product
import nu.westlin.eshop.catalog.internal.ProductRepository
import nu.westlin.eshop.common.ProductId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CatalogFacade(private val productRepository: ProductRepository) {

    @Transactional
    fun createProduct(command: CreateProductCommand) {
        productRepository.insert(command.toProduct())
    }

    fun exists(id: ProductId): Boolean = productRepository.exists(id)

    fun allProductsExist(productIds: List<ProductId>): AllProductsExistResponse {
        val notExistingProductIds: List<ProductId> = productIds.filterNot { productRepository.exists(it) }
        return if (notExistingProductIds.isEmpty()) {
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

data class CreateProductCommand(val productId: ProductId, val name: String, val description: String, val price: Int) {
    companion object
}

fun CreateProductCommand.toProduct(): Product = Product(
    id = productId,
    name = name,
    description = description,
    price = price,
)
