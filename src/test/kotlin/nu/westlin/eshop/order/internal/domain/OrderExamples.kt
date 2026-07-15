package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.customer.Percentage
import kotlin.random.Random

fun Order.Companion.example(
    id: OrderId = OrderId.generate(),
    customerId: CustomerId = CustomerId.generate(),
    status: OrderStatus = OrderStatus.Pending,
    items: OrderLineItems = OrderLineItems.example(),
    discount: Percentage = Percentage(0.0),
): Order = Order(
    id = id,
    customerId = customerId,
    status = status,
    items = items,
    discount = discount,
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

fun OrderLineItems.Companion.example(
    value: Set<OrderLineItem> = List(3) { OrderLineItem.example() }.toSet(),
): OrderLineItems = OrderLineItems(
    value = value,
)