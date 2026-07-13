package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.apache.commons.lang3.RandomStringUtils

fun Customer.Companion.example(
    id: CustomerId = CustomerId.generate(),
    name: String = RandomStringUtils.secure().nextAlphabetic(10),
): Customer = Customer(
    id = id,
    name = name,
)