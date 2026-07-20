package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataReservedInventoryItemRepository :
    ListCrudRepository<ReservedInventoryItem, ReservedInventoryItemId> {

    fun findByIdOrderId(orderId: OrderId): List<ReservedInventoryItem>
    fun findByIdProductId(productId: ProductId): List<ReservedInventoryItem>
}

@Repository
class ReservedInventoryItemRepository(
    private val springDataRepository: SpringDataReservedInventoryItemRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(item: ReservedInventoryItem) {
        entityTemplate.insert(item)
    }

    fun findByOrderId(orderId: OrderId): List<ReservedInventoryItem> = springDataRepository.findByIdOrderId(orderId)
    fun findByProductId(productId: ProductId): List<ReservedInventoryItem> = springDataRepository.findByIdProductId(
        productId,
    )

    fun delete(reservedItem: ReservedInventoryItem) {
        springDataRepository.delete(reservedItem)
    }
}