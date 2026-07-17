package nu.westlin.eshop.common

import nu.westlin.eshop.test.EmailGenerator
import org.apache.commons.lang3.RandomStringUtils

fun NewCustomerRegisteredEvent.Companion.example(
    customerId: CustomerId = CustomerId.generate(),
    name: String = RandomStringUtils.secure().nextAlphabetic(6),
    email: String = EmailGenerator.generate(),
    username: String = RandomStringUtils.secure().nextAlphabetic(6),
    password: String = RandomStringUtils.secure().nextAlphabetic(6),
): NewCustomerRegisteredEvent = NewCustomerRegisteredEvent(
    customerId = customerId,
    name = name,
    email = email,
    username = username,
    password = password,
)