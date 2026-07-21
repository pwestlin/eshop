package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.instantNowTruncated
import java.time.Instant

fun CustomerOrder.Companion.example(
    customerId: CustomerId = CustomerId.generate(),
    orderId: OrderId = OrderId.generate(),
    grandTotal: Int = 42,
    instant: Instant = instantNowTruncated(),
): CustomerOrder = CustomerOrder(
    customerId = customerId,
    orderId = orderId,
    grandTotal = grandTotal,
    instant = instant,
)
