package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataCustomerRepository : ListCrudRepository<Customer, CustomerId>

@Repository
class CustomerRepository(
    private val springDataRepository: SpringDataCustomerRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(customer: Customer) {
        entityTemplate.insert(customer)
    }

    fun exists(id: CustomerId): Boolean = springDataRepository.existsById(id)

    fun findAll(): List<Customer> = springDataRepository.findAll()
}