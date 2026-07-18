package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import kotlin.random.Random

fun ReservedInventoryItem.Companion.example(
    id: ReservedInventoryItemId = ReservedInventoryItemId.example(),
    quantity: Int = Random.nextInt(1, 50),
): ReservedInventoryItem = ReservedInventoryItem(
    id = id,
    quantity = quantity,
)

fun ReservedInventoryItemId.Companion.example(
    productId: ProductId = ProductId(Random.nextInt(1, Int.MAX_VALUE - 1)),
    orderId: OrderId = OrderId.generate(),
): ReservedInventoryItemId = ReservedInventoryItemId(
    productId = productId,
    orderId = orderId,
)