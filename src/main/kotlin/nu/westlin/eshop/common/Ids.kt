package nu.westlin.eshop.common

import java.util.*

@JvmInline
value class OrderId(val value: UUID) {
    companion object {
        fun generate(): OrderId = OrderId(UUID.randomUUID())
    }
}

@JvmInline
value class ProductId(val value: UUID) {
    companion object {
        fun generate(): ProductId = ProductId(UUID.randomUUID())
    }
}

@JvmInline
value class CustomerId(val value: UUID) {
    companion object {
        fun generate(): CustomerId = CustomerId(UUID.randomUUID())
    }
}