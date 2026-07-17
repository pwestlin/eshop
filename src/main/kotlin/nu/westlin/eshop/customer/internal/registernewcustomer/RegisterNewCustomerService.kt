package nu.westlin.eshop.customer.internal.registernewcustomer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.NewCustomerRegisteredEvent
import nu.westlin.eshop.customer.internal.Customer
import nu.westlin.eshop.customer.internal.CustomerRepository
import nu.westlin.eshop.customer.internal.Email
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterNewCustomerService(
    private val customerRepository: CustomerRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun register(request: NewCustomerRequest): NewCustomerRequestResult =
        if (customerRepository.findByEmail(Email(request.email)) == null) {
            val customer = Customer(
                id = CustomerId.generate(),
                name = request.name,
                email = Email(request.email),
            )
            customerRepository.insert(customer)
            eventPublisher.publishEvent(
                NewCustomerRegisteredEvent(
                    customerId = customer.id,
                    name = customer.name,
                    email = request.email,
                    username = request.username,
                    password = request.password,
                ),
            )
            NewCustomerRequestResult.Ok(customer.id)
        } else {
            NewCustomerRequestResult.Duplicate
        }
}

sealed interface NewCustomerRequestResult {
    data class Ok(val customerId: CustomerId) : NewCustomerRequestResult
    data object Duplicate : NewCustomerRequestResult
}