package nu.westlin.eshop.common

import nu.westlin.eshop.order.OrderPlacedEvent
import nu.westlin.eshop.order.OrderPlacedEvent.OrderPlacedItem
import java.time.Instant
import kotlin.random.Random

fun OrderPlacedEvent.Companion.example(
    orderId: OrderId = OrderId.generate(),
    customerId: CustomerId = CustomerId.generate(),
    items: Set<OrderPlacedItem> = List(3) {
        OrderPlacedItem(
            productId = ProductId.generate(),
            quantity = Random.nextInt(1, 50),
        )
    }.toSet(),
    occurredAt: Instant = instantNowTruncated().minusSeconds(5),
): OrderPlacedEvent = OrderPlacedEvent(
    orderId = orderId,
    customerId = customerId,
    items = items,
    occurredAt = occurredAt,
)