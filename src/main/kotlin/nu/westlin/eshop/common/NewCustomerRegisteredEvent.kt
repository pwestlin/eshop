package nu.westlin.eshop.common

// TODO pwestlin: Vem ska hantera detta och var ska det ligga?
data class NewCustomerRegisteredEvent(
    val customerId: CustomerId,
    val name: String,
    val email: String,
    val username: String,
    val password: String,
) {
    companion object
}
