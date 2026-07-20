package nu.westlin.eshop.order.internal.domain

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import java.time.Instant
import kotlin.random.Random

@Suppress("LongParameterList")
fun Order.Companion.example(
    id: OrderId = OrderId.generate(),
    createdAt: Instant = instantNowTruncated(),
    updatedAt: Instant = createdAt,
    customerId: CustomerId = CustomerId.generate(),
    status: OrderStatus = OrderStatus.PENDING,
    items: OrderLineItems = OrderLineItems.example(),
    discount: Percentage = Percentage(0.0),
    shippedTime: Instant? = null,
): Order = Order(
    id = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
    customerId = customerId,
    status = status,
    items = items,
    discount = discount,
    shippedTime = shippedTime,
)

fun OrderLineItem.Companion.example(
    id: Long = Random.nextLong(1, Long.MAX_VALUE - 1),
    productId: ProductId = ProductId.generate(),
    quantity: Int = Random.nextInt(1, 50),
    price: Int = Random.nextInt(10, 2_000),
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