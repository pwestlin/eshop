package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.ProductId
import kotlin.random.Random

fun InventoryItem.Companion.example(
    productId: ProductId = ProductId.generate(),
    quantity: Int = Random.nextInt(1, 50),
): InventoryItem = InventoryItem(
    productId = productId,
    quantity = quantity,
)