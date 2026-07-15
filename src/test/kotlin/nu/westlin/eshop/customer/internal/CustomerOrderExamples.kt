package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

fun CustomerOrder.Companion.example(
    customerId: CustomerId = CustomerId.generate(),
    orderId: UUID = UUID.randomUUID(),
    totalPrice: Int = 42,
    instant: Instant = Instant.now().truncatedTo(ChronoUnit.MICROS),
): CustomerOrder = CustomerOrder(
    customerId = customerId,
    orderId = orderId,
    totalPrice = totalPrice,
    instant = instant,
)
