package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.instantNowTruncated
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

// TODO pwestlin: Allt i denna behöver inte vara pubikt för MODULEN (ex loyaltyDiscount).
@Service
class CustomerLoyaltyService(private val customerOrderRepository: CustomerOrderRepository) {

    fun loyaltyDiscount(customerId: CustomerId): DiscountTier {
        val orders = customerOrderRepository.findAllByCustomerIdAndInstantGreaterThanEqual(
            customerId,
            // TODO pwestlin: Read Duration from application.yml
            instantNowTruncated().minus(Duration.ofDays(365)),
        )
        val ordersTotalSum = orders.sumOf { it.totalPrice }
        return DiscountTier.fromTotalSum(ordersTotalSum)
    }

    @Transactional
    fun store(customerId: CustomerId, orderId: OrderId, totalPrice: Int, instant: Instant) {
        val customerOrder = CustomerOrder(
            customerId = customerId,
            orderId = orderId,
            totalPrice = totalPrice,
            instant = instant,
        )
        customerOrderRepository.insert(customerOrder)
    }
}