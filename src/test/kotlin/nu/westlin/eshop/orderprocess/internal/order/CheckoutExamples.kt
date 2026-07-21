package nu.westlin.eshop.orderprocess.internal.order

import nu.westlin.eshop.common.Money
import java.util.*
import kotlin.random.Random

fun CheckoutRequest.Companion.example(
    orderId: UUID = UUID.randomUUID(),
    customerId: UUID = UUID.randomUUID(),
    items: Set<CheckoutRequest.Item> = setOf(
        CheckoutRequest.Item(
            productId = Random.nextInt(1, Int.MAX_VALUE - 1),
            quantity = 42,
            price = Money.sek(7),
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