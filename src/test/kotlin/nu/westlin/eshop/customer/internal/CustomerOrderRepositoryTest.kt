package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.time.temporal.ChronoUnit

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
    SharedTestcontainersConfiguration::class,
    CustomerOrderRepository::class,
    CustomerSpringDataJdbcConfiguration::class,
)
class CustomerOrderRepositoryTest @Autowired constructor(
    private val repository: CustomerOrderRepository,
    private val springDataRepository: SpringDataCustomerOrderRepository,
) {

    @Test
    fun `insert new`() {
        val customerOrder = CustomerOrder.example()
        val created = repository.insert(customerOrder)

        assertThat(springDataRepository.findByIdOrNull(created.id!!)).isEqualTo(created)
    }

    @Test
    fun `findAllByCustomerIdAndInstantGreaterThanEqual - should find two`() {
        val now = Instant.now().truncatedTo(ChronoUnit.MICROS)
        val customerId = CustomerId.generate()
        repository.insert(CustomerOrder.example(customerId = customerId, instant = now.minusSeconds(42)))
        val customerOrder2 = repository.insert(
            CustomerOrder.example(customerId = customerId, instant = now.minusSeconds(25)),
        )
        val customerOrder3 = repository.insert(
            CustomerOrder.example(customerId = customerId, instant = now.minusSeconds(12)),
        )

        val result = repository.findAllByCustomerIdAndInstantGreaterThanEqual(
            customerId = customerId,
            since = now.minusSeconds(30),
        )
        assertThat(result).containsExactlyInAnyOrder(customerOrder2, customerOrder3)
    }
}