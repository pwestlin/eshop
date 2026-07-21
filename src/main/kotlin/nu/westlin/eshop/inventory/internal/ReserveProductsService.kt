package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.inventory.ProductsReservation
import nu.westlin.eshop.inventory.TooFewProducts
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReserveProductsService(
    private val inventoryItemRepository: InventoryItemRepository,
    private val reservedInventoryItemRepository: ReservedInventoryItemRepository,
) {

    fun areProductsAvailable(orderedItems: Set<ProductsReservation.Item>): Set<TooFewProducts> =
        orderedItems.mapNotNull { orderedItem ->
            val inventoryItem = inventoryItemRepository.getById(orderedItem.productId)
            val reservedQuantity =
                reservedInventoryItemRepository.findByProductId(orderedItem.productId).sumOf { it.quantity }
            val actualQuantityInStock = inventoryItem.quantity - reservedQuantity
            if (actualQuantityInStock >= orderedItem.quantity) {
                null
            } else {
                TooFewProducts(
                    productId = orderedItem.productId,
                    orderQuantity = orderedItem.quantity,
                    inventoryQuantity = actualQuantityInStock,
                )
            }
        }.toSet()

    @Transactional
    fun reserveProducts(orderId: OrderId, orderedItems: Set<ProductsReservation.Item>) {
        orderedItems.forEach { item ->
            reservedInventoryItemRepository.insert(
                ReservedInventoryItem(
                    id = ReservedInventoryItemId(
                        productId = item.productId,
                        orderId = orderId,
                    ),
                    quantity = item.quantity,
                ),
            )
        }
    }

    @Transactional
    fun completeReservation(orderId: OrderId) {
        val reservedItems = reservedInventoryItemRepository.findByOrderId(orderId)
        reservedItems.forEach { reservedItem ->
            val item = inventoryItemRepository.getById(reservedItem.id.productId)
            inventoryItemRepository.update(item.copy(quantity = item.quantity - reservedItem.quantity))
            reservedInventoryItemRepository.delete(reservedItem)
        }
    }

    @Transactional
    fun cancelReservation(orderId: OrderId) {
        reservedInventoryItemRepository.findByOrderId(orderId).forEach { item ->
            reservedInventoryItemRepository.delete(item)
        }
    }
}