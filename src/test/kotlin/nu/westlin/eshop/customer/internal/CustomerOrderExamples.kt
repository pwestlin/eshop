package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.instantNowTruncated
import java.time.Instant
import java.util.UUID

fun CustomerOrder.Companion.example(
    customerId: CustomerId = CustomerId.generate(),
    orderId: UUID = UUID.randomUUID(),
    totalPrice: Int = 42,
    instant: Instant = instantNowTruncated(),
): CustomerOrder = CustomerOrder(
    customerId = customerId,
    orderId = orderId,
    totalPrice = totalPrice,
    instant = instant,
)
