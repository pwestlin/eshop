package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.order.internal.OrderSpringDataJdbcConfiguration
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderStatus
import nu.westlin.eshop.order.internal.domain.example
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SharedTestcontainersConfiguration::class, OrderRepository::class, OrderSpringDataJdbcConfiguration::class)
class OrderRepositoryTest @Autowired constructor(private val orderRepository: OrderRepository) {

    @Test
    fun `find by id ska returnera en`() {
        val order = Order.example()
        val createdOrder = orderRepository.insert(order)
        assertThat(createdOrder.id).isEqualTo(order.id)
        assertThat(orderRepository.findById(order.id)).isEqualTo(createdOrder)
    }

    @Test
    fun `update should update`() {
        val order = Order.example()
        val createdOrder = orderRepository.insert(order)

        val updatedOrder = createdOrder.copy(status = OrderStatus.SHIPPED, shippedTime = instantNowTruncated())
        orderRepository.update(updatedOrder)
        val actual = orderRepository.findById(order.id)
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(updatedOrder.copy(version = 2))
    }

    @Test
    fun `findByCustomerId should return none`() {
        repeat(3) {
            orderRepository.insert(Order.example())
        }
        assertThat(orderRepository.findByCustomerId(CustomerId.generate())).isEmpty()
    }

    @Test
    fun `findByCustomerId should return two`() {
        repeat(3) {
            orderRepository.insert(Order.example())
        }
        val customerId = CustomerId.generate()
        val orders = List(2) {
            Order.example(customerId = customerId)
        }
        orders.forEach { orderRepository.insert(it) }

        assertThat(orderRepository.findByCustomerId(customerId)).containsExactlyInAnyOrderElementsOf(orders)
    }
}