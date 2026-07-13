package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import kotlin.random.Random

fun Order.Companion.example(
    id: OrderId = OrderId.generate(),
    customerId: CustomerId = CustomerId.generate(),
    status: OrderStatus = OrderStatus.Pending,
    items: Set<OrderLineItem> = List(3) { OrderLineItem.example() }.toSet(),
): Order = Order(
    id = id,
    customerId = customerId,
    status = status,
    items = items,
)

fun OrderLineItem.Companion.example(
    id: Long = Random.nextLong(1, Long.MAX_VALUE - 1),
    productId: ProductId = ProductId.generate(),
    quantity: Int = 42,
    price: Int = 5,
): OrderLineItem = OrderLineItem(
    id = id,
    productId = productId,
    quantity = quantity,
    price = price,
)