package nu.westlin.eshop.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId

data class PaymentDetails(
    val customerId: CustomerId,
    // TODO pwestlin: Använd en typ (Money eller nåt liknande) isf Int överallt.
    val totalAmount: Int
) {
    companion object
}