package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.order.internal.domain.Order
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface SpringDataOrderRepository : ListCrudRepository<Order, OrderId> {
    fun findByCustomerId(customerid: CustomerId): List<Order>
}

@Repository
class OrderRepository(
    private val springDataRepository: SpringDataOrderRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {

    fun findById(id: OrderId): Order? = springDataRepository.findByIdOrNull(id)

    fun insert(order: Order): Order = entityTemplate.insert(order)
    fun update(order: Order): Order = entityTemplate.update(order)
    fun findByCustomerId(customerid: CustomerId): List<Order> = springDataRepository.findByCustomerId(customerid)
}