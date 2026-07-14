package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.ProductId
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

fun Product.Companion.example(
    id: ProductId = ProductId.generate(),
    name: String = RandomStringUtils.secure().nextAlphabetic(10),
    description: String = RandomStringUtils.secure().nextAlphabetic(10),
    price: Int = Random.nextInt(50, 10_000),
): Product = Product(
    id = id,
    name = name,
    description = description,
    price = price,
)