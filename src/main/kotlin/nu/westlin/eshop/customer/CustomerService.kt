package nu.westlin.eshop.customer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.customer.internal.CustomerLoyaltyService
import nu.westlin.eshop.customer.internal.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val customerLoyaltyService: CustomerLoyaltyService,
) {

    fun exists(id: CustomerId): Boolean = customerRepository.exists(id)

    /**
     * @throws IllegalArgumentException if customer with [id] does not exist.
     */
    fun discount(id: CustomerId): CustomerDiscountDto {
        val discountTier = customerLoyaltyService.loyaltyDiscount(id)
        return CustomerDiscountDto(
            tier = discountTier.name,
            rate = discountTier.rate,
        )
    }
}