package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface SpringDataInventoryItemRepository : ListCrudRepository<InventoryItem, ProductId>

@Repository
class InventoryItemRepository(
    private val springDataRepository: SpringDataInventoryItemRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(inventoryItem: InventoryItem): InventoryItem = entityTemplate.insert(inventoryItem)

    fun update(inventoryItem: InventoryItem): InventoryItem = entityTemplate.update(inventoryItem)

    /**
     * @throws IllegalArgumentException if [productId] can't be found.
     */
    fun getById(productId: ProductId): InventoryItem = springDataRepository.findByIdOrNull(
        productId,
    ) ?: throw IllegalArgumentException("InventoryItem with id $productId does not exist")
}