package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CurrencySpringDataJdbcConfiguration
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(
    SharedTestcontainersConfiguration::class,
    CustomerRepository::class,
    CustomerSpringDataJdbcConfiguration::class,
    CurrencySpringDataJdbcConfiguration::class,
)
class CustomerRepositoryTest @Autowired constructor(private val repository: CustomerRepository) {

    @Test
    fun `exists returns false if none is found`() {
        assertThat(repository.exists(CustomerId.generate())).isFalse
    }

    @Test
    fun `exists returns true if one is found`() {
        val customer = Customer.example()
        repository.insert(customer)
        assertThat(repository.exists(customer.id)).isTrue
    }

    @Test
    fun `getById returns customer`() {
        val customer = Customer.example()
        repository.insert(customer)

        assertThat(repository.getById(customer.id)).isEqualTo(customer)
    }

    @Test
    fun `getById throws IllegalArgumentException if customer does not exist`() {
        val customerId = CustomerId.generate()
        assertThatThrownBy { repository.getById(customerId) }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("Customer with id $customerId does not exist")
    }
}