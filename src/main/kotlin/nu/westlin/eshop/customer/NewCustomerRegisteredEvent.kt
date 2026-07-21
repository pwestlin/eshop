package nu.westlin.eshop.customer

import nu.westlin.eshop.common.CustomerId

data class NewCustomerRegisteredEvent(
    val customerId: CustomerId,
    val name: String,
    val email: String,
    val username: String,
    val password: String,
) {
    companion object
}
