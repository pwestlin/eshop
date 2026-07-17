package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface SpringDataCustomerRepository : ListCrudRepository<Customer, CustomerId> {
    fun findByEmail(email: Email): Customer?
}

@Repository
class CustomerRepository(
    private val springDataRepository: SpringDataCustomerRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(customer: Customer) {
        entityTemplate.insert(customer)
    }

    fun exists(id: CustomerId): Boolean = springDataRepository.existsById(id)

    fun getById(id: CustomerId): Customer = springDataRepository.findByIdOrNull(
        id,
    ) ?: throw IllegalArgumentException("Customer with id $id does not exist")

    fun findByIdOrNull(id: CustomerId): Customer? = springDataRepository.findByIdOrNull(id)

    fun findByEmail(email: Email): Customer? = springDataRepository.findByEmail(email)
}