package nu.westlin.eshop.orderprocess.internal.order

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*

@RestController
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/orders")
class CheckoutOrderController(private val checkoutOrderService: CheckoutOrderService) {

    // TODO pwestlin: Fixa konstanter för roller
    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkout(@RequestBody request: CheckoutRequest): ResponseEntity<Any> {
        // TODO pwestlin: Kolla om OrderId redan finns (idempotens)?

        val result = checkoutOrderService.processCheckout(
            orderId = OrderId(request.orderId),
            customerId = CustomerId(request.customerId),
            items = request.items,
        )

        return when (result) {
            is CheckoutResult.Success -> {
                val location: URI = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(request.orderId)
                    .toUri()
                ResponseEntity.created(location).body(CheckoutResponse(orderId = request.orderId))
            }

            is CheckoutResult.OrderAlreadyExist, is CheckoutResult.CustomerDoesNotExist, is CheckoutResult.ProductsDoesNotExist -> ResponseEntity.badRequest()
                .body(
                    result,
                )
        }
    }
}

data class CheckoutRequest(val orderId: UUID, val customerId: UUID, val items: Set<Item>) {

    data class Item(val productId: Int, val quantity: Int, val price: Int) {
        companion object
    }

    companion object
}

data class CheckoutResponse(val orderId: UUID) {
    companion object
}