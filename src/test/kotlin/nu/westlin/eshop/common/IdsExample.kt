package nu.westlin.eshop.common

import nu.westlin.eshop.order.internal.checkout.CheckoutRequest
import nu.westlin.eshop.order.internal.checkout.CheckoutRequest.CheckoutItemRequest
import nu.westlin.eshop.order.internal.checkout.CheckoutResponse
import java.util.*

fun CheckoutRequest.Companion.example(
    orderId: UUID = UUID.randomUUID(),
    customerId: UUID = UUID.randomUUID(),
    items: Set<CheckoutItemRequest> = setOf(
        CheckoutItemRequest(
            productId = UUID.randomUUID(),
            quantity = 42,
            price = 7,
        ),
    ),
): CheckoutRequest = CheckoutRequest(
    orderId = orderId,
    customerId = customerId,
    items = items,
)

fun CheckoutResponse.Companion.example(orderId: UUID = UUID.randomUUID()): CheckoutResponse = CheckoutResponse(
    orderId = orderId,
)