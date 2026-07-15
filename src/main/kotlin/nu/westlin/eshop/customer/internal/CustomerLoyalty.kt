package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderShippedEvent
import nu.westlin.eshop.common.logger
import nu.westlin.eshop.customer.Percentage
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.ListCrudRepository
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class CustomerLoyaltyService(private val customerOrderRepository: CustomerOrderRepository) {
    private val logger = logger()

    fun loyaltyDiscount(customerId: CustomerId): DiscountTier {
        val orders = customerOrderRepository.findAllByCustomerIdAndInstantGreaterThanEqual(customerId, Instant.now())
        val ordersTotalSum = orders.sumOf { it.totalPrice }
        return DiscountTier.fromTotalSum(ordersTotalSum)
    }

    @ApplicationModuleListener
    fun handleOrderShippedEvent(event: OrderShippedEvent) {
        logger.info("event: $event")
        customerOrderRepository.insert(event.toCustomerOrder())
    }
}

fun OrderShippedEvent.toCustomerOrder(): CustomerOrder = CustomerOrder(
    customerId = customerId,
    orderId = orderId.value,
    totalPrice = totalPrice,
    instant = occurredAt,
)

@Repository
interface SpringDataCustomerOrderRepository : ListCrudRepository<CustomerOrder, Int> {

    // Genererar automatiskt: SELECT * FROM customer_order WHERE customer_id = :customerId AND instant >= :since
    fun findAllByCustomerIdAndInstantGreaterThanEqual(customerId: CustomerId, since: Instant): List<CustomerOrder>
}

@Repository
class CustomerOrderRepository(
    private val springDataRepository: SpringDataCustomerOrderRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun insert(customerOrder: CustomerOrder): CustomerOrder = entityTemplate.insert(customerOrder)

    fun findAllByCustomerIdAndInstantGreaterThanEqual(customerId: CustomerId, since: Instant): List<CustomerOrder> =
        springDataRepository.findAllByCustomerIdAndInstantGreaterThanEqual(
            customerId = customerId,
            since = since,
        )
}

@Table("customer_orders")
data class CustomerOrder(
    @Id
    val id: Int? = null,
    val customerId: CustomerId,
    // TODO pwestlin: OrderId isf UUID? Då måste jag ha en konverterare för OrderId även i denna modul.
    val orderId: UUID,
    val totalPrice: Int,
    val instant: Instant,
) {
    companion object
}

@Suppress("MagicNumber")
enum class DiscountTier(val threshold: Int, val rate: Percentage) {
    None(0, Percentage(0.0)),
    Bronze(10_000, Percentage(0.05)),
    Silver(25_000, Percentage(0.10)),
    Gold(100_000, Percentage(0.20)),
    ;

    companion object {
        fun fromTotalSum(totalSum: Int): DiscountTier =
            entries.sortedByDescending { it.threshold }.firstOrNull { totalSum >= it.threshold } ?: None
    }
}