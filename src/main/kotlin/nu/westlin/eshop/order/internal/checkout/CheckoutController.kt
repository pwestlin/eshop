package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.order.internal.checkout.ProcessCheckoutResult.CustomerDoesNotExist
import nu.westlin.eshop.order.internal.checkout.ProcessCheckoutResult.Ok
import nu.westlin.eshop.order.internal.checkout.ProcessCheckoutResult.OrderAlreadyExist
import nu.westlin.eshop.order.internal.checkout.ProcessCheckoutResult.ProductsDoesNotExist
import nu.westlin.eshop.order.internal.domain.OrderLineItem
import nu.westlin.eshop.order.internal.domain.OrderLineItems
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/orders")
class CheckoutController(private val checkoutService: CheckoutService) {

    @GetMapping("/id/create", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrderId(): UUID = OrderId.generate().value

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkout(@RequestBody request: CheckoutRequest): ResponseEntity<Any> {
        val result = checkoutService.processCheckout(
            orderId = OrderId(request.orderId),
            customerId = CustomerId(request.customerId),
            items = request.toDomainItems(),
        )

        return when (result) {
            is Ok -> {
                val location: URI = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(result.order.id.value)
                    .toUri()
                ResponseEntity.created(location).body(CheckoutResponse(orderId = result.order.id.value))
            }

            is OrderAlreadyExist, is CustomerDoesNotExist, is ProductsDoesNotExist -> ResponseEntity.badRequest().body(
                result,
            )
        }
    }
}

data class CheckoutRequest(val orderId: UUID, val customerId: UUID, val items: Set<CheckoutItemRequest>) {

    data class CheckoutItemRequest(val productId: Int, val quantity: Int, val price: Int) {
        companion object
    }

    companion object
}

fun CheckoutRequest.toDomainItems(): OrderLineItems = OrderLineItems(items.map { it.toDomainItem() }.toSet())

fun CheckoutRequest.CheckoutItemRequest.toDomainItem(): OrderLineItem = OrderLineItem(
    id = null,
    productId = ProductId(productId),
    quantity = quantity,
    price = price,
)

data class CheckoutResponse(val orderId: UUID) {
    companion object
}