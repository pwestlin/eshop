package nu.westlin.eshop.customer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.customer.internal.CustomerLoyaltyService
import nu.westlin.eshop.customer.internal.CustomerOrder
import nu.westlin.eshop.customer.internal.CustomerOrderRepository
import nu.westlin.eshop.customer.internal.CustomerRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

// TODO pwestlin: Ta bort (eller döp om till @Component FooFacade) alla publika @Service


@Component
class CustomerFacade(
    private val customerLoyaltyService: CustomerLoyaltyService,
    private val customerRepository: CustomerRepository,
    private val customerOrderRepository: CustomerOrderRepository
) {

    fun getActiveDiscountFor(customerId: CustomerId): CustomerDiscount {
        val discountTier = customerLoyaltyService.loyaltyDiscount(customerId)
        return CustomerDiscount(
            tier = discountTier.name,
            rate = discountTier.rate,
        )
    }

    fun customerExists(customerId: CustomerId): Boolean = customerRepository.exists(customerId)

    @Transactional
    fun storeCustomerOrderHistory(
        customerId: CustomerId,
        orderId: OrderId,
        totalPrice: Int,
        instant: Instant,
    ) {
        val customerOrder = CustomerOrder(
            customerId = customerId,
            orderId = orderId,
            totalPrice = totalPrice,
            instant = instant
        )
        customerOrderRepository.insert(customerOrder)
    }
}