package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.Money
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

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
    val orderId: OrderId,
    @Embedded.Nullable(prefix = "grand_total_")
    val grandTotal: Money,
    val instant: Instant,
) {
    companion object {
        fun new(customerId: CustomerId, orderId: OrderId, grandTotal: Money, instant: Instant): CustomerOrder =
            CustomerOrder(
                customerId = customerId,
                orderId = orderId,
                grandTotal = grandTotal,
                instant = instant,
            )
    }
}

@Suppress("MagicNumber")
enum class DiscountTier(val threshold: Money, val rate: Percentage) {
    NONE(Money.sek(0), Percentage.ZERO),
    BRONZE(Money.sek(10_000), Percentage(0.05)),
    SILVER(Money.sek(25_000), Percentage(0.10)),
    GOLD(Money.sek(100_000), Percentage(0.20)),
    ;

    companion object {
        fun fromTotalSum(totalSum: Money): DiscountTier = entries.lastOrNull { totalSum >= it.threshold } ?: NONE
    }
}