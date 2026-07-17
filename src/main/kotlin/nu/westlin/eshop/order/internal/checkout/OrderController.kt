package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/orders")
class OrderController(private val orderRepository: OrderRepository) {

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOrdersByOrderId(@PathVariable id: UUID): ResponseEntity<OrderDTO> {
        val order = orderRepository.findById(OrderId(id))
        return if (order != null) {
            ResponseEntity.ok(order.toDto())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOrdersByCustomerId(@PathVariable("id") customerId: UUID): GetOrdersByCustomerIdResponse {
        val orders = orderRepository.findByCustomerId(CustomerId(customerId))
        val getOrdersByCustomerIdResponse =
            GetOrdersByCustomerIdResponse(orders.map { it.toDto() }.sortedByDescending { it.createdAt })
        return getOrdersByCustomerIdResponse
    }
}

data class OrderDTO(
    val orderid: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: OrderStatus,
    val totalPrice: Int,
    val shippedTime: Instant? = null,
)

fun Order.toDto(): OrderDTO = OrderDTO(
    orderid = this.id.value,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    status = this.status,
    totalPrice = this.totalPrice,
    shippedTime = this.shippedTime,
)

data class GetOrdersByCustomerIdResponse(val orders: List<OrderDTO>)
