package nu.westlin.eshop.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money

data class PaymentDetails(val customerId: CustomerId, val totalAmount: Money) {
    companion object
}