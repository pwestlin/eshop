package nu.westlin.eshop.customer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.Percentage
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.customer.internal.Customer
import nu.westlin.eshop.customer.internal.CustomerOrder
import nu.westlin.eshop.customer.internal.CustomerRepository
import nu.westlin.eshop.customer.internal.CustomerSpringDataJdbcConfiguration
import nu.westlin.eshop.customer.internal.SpringDataCustomerOrderRepository
import nu.westlin.eshop.customer.internal.example
import nu.westlin.eshop.test.ModulithIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.findByIdOrNull
import java.time.Duration
import java.time.Instant

@ModulithIntegrationTest
@Import(CustomerSpringDataJdbcConfiguration::class)
class CustomerModuleIntegrationTest @Autowired constructor(
    private val customerFacade: CustomerFacade,
    private val customerRepository: CustomerRepository,
    private val springDataCustomerOrderRepository: SpringDataCustomerOrderRepository,
    private val entityTemplate: JdbcAggregateTemplate
) {

    @Test
    fun `customerExists - false`() {
        assertThat(customerFacade.customerExists(CustomerId.generate())).isFalse
    }

    @Test
    fun `customerExists - true`() {
        val customer = Customer.example()
        customerRepository.insert(customer)

        assertThat(customerFacade.customerExists(customer.id)).isTrue
    }

    @Test
    fun `store Customer Order History`() {
        val customerId = CustomerId.generate()
        val orderId = OrderId.generate()
        val totalPrice = 46
        val instant = instantNowTruncated()
        val expected = CustomerOrder(
            customerId = customerId,
            orderId = orderId,
            totalPrice = totalPrice,
            instant = instant
        )
        val inserted = customerFacade.storeCustomerOrderHistory(
            customerId = customerId,
            orderId = orderId,
            totalPrice = totalPrice,
            instant = instant
        )
        assertThat(inserted)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected)
        assertThat(springDataCustomerOrderRepository.findByIdOrNull(inserted.id!!))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected)
    }

    @Test
    fun `getActiveDiscountFor Customer - no orders found for customer`() {
        val customerId = CustomerId.generate()
        val discount = customerFacade.getActiveDiscountFor(customerId)
        assertThat(discount).isEqualTo(
            CustomerDiscount(
                tier = "NONE",
                rate = Percentage(0.0)
            )
        )
    }

    @Test
    fun `getActiveDiscountFor Customer - no discount for customer`() {
        val customerOrder = CustomerOrder.example(
            instant = Instant.now().minus(Duration.ofDays(1_000))
        )
        entityTemplate.insert(customerOrder)

        val discount = customerFacade.getActiveDiscountFor(customerOrder.customerId)
        assertThat(discount).isEqualTo(
            CustomerDiscount(
                tier = "NONE",
                rate = Percentage(0.0)
            )
        )
    }

    @Test
    fun `getActiveDiscountFor Customer - bronze`() {
        val customerOrder = CustomerOrder.example(
            totalPrice = 15_000,
            instant = Instant.now().minus(Duration.ofDays(69))
        )
        entityTemplate.insert(customerOrder)

        val discount = customerFacade.getActiveDiscountFor(customerOrder.customerId)
        assertThat(discount).isEqualTo(
            CustomerDiscount(
                tier = "BRONZE",
                rate = Percentage(0.05)
            )
        )
    }
}