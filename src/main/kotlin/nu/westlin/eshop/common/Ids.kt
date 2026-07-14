package nu.westlin.eshop.common

import java.util.*
import kotlin.random.Random

@JvmInline
value class OrderId(val value: UUID) {
    companion object {
        fun generate(): OrderId = OrderId(UUID.randomUUID())
    }
}

@JvmInline
value class ProductId(val value: Int) {

    init {
        require(value > 0) { "value '$value' must be > 0" }
    }

    companion object {
        fun generate(): ProductId = ProductId(Random.nextInt(1, Int.MAX_VALUE - 1))
    }
}

@JvmInline
value class CustomerId(val value: UUID) {
    companion object {
        fun generate(): CustomerId = CustomerId(UUID.randomUUID())
    }
}