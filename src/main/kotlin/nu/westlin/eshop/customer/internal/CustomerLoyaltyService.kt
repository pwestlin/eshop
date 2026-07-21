package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.instantNowTruncated
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CustomerLoyaltyService(
    private val customerOrderRepository: CustomerOrderRepository,
    @Value($$"${customer.loyalty.duration}") private val loyaltyDuration: Duration,
) {

    fun loyaltyDiscount(customerId: CustomerId): DiscountTier {
        val orders = customerOrderRepository.findAllByCustomerIdAndInstantGreaterThanEqual(
            customerId,
            instantNowTruncated().minus(loyaltyDuration),
        )
        val ordersTotalSum = orders.sumOf { it.grandTotal }
        return DiscountTier.fromTotalSum(ordersTotalSum)
    }
}