package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money
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
        val ordersTotalSum = orders
            .map { it.grandTotal }
            .reduceOrNull { acc, money -> acc + money }
            ?: Money.zero()
        return DiscountTier.fromTotalSum(ordersTotalSum)
    }
}