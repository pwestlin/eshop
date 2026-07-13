package nu.westlin.eshop.customer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.customer.internal.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun exists(id: CustomerId): Boolean = customerRepository.exists(id)
}