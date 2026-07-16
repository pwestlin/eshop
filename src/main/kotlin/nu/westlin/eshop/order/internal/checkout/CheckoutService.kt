package nu.westlin.eshop.order.internal.checkout

import nu.westlin.eshop.catalog.CatalogService
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.common.instantNowTruncated
import nu.westlin.eshop.customer.CustomerService
import nu.westlin.eshop.order.internal.domain.Order
import nu.westlin.eshop.order.internal.domain.OrderLineItems
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutService(
    private val orderRepository: OrderRepository,
    private val customerService: CustomerService,
    private val eventPublisher: ApplicationEventPublisher,
    private val catalogService: CatalogService,
) {

    @Transactional
    @Suppress("ReturnCount")
    fun processCheckout(customerId: CustomerId, items: OrderLineItems, orderId: OrderId): ProcessCheckoutResult {
        if (!customerService.exists(customerId)) {
            return ProcessCheckoutResult.CustomerDoesNotExist(customerId)
        }

        val notExistingProductIds = items.value.mapNotNull { item ->
            if (catalogService.exists(item.productId)) {
                null
            } else {
                item.productId
            }
        }.toSet()
        if (notExistingProductIds.isNotEmpty()) {
            return ProcessCheckoutResult.ProductsDoesNotExist(notExistingProductIds)
        }

        val discount = customerService.discount(customerId)

        val order = Order.new(
            id = orderId,
            customerId = customerId,
            items = items,
            discount = discount.rate,
        )
        val createdOrder = orderRepository.insert(order)
        println("order = $order")
        println("createdOrder = $createdOrder")
        eventPublisher.publishEvent(
            OrderPlacedEvent(
                orderId = order.id,
                customerId = order.customerId,
                items = order.items.value.map { item ->
                    OrderPlacedEvent.OrderPlacedItem(
                        productId = item.productId,
                        quantity = item.quantity,
                    )
                }.toSet(),
                occurredAt = instantNowTruncated(),
            ),
        )
        return ProcessCheckoutResult.Ok(createdOrder)
    }
}

sealed interface ProcessCheckoutResult {

    data class Ok(val order: Order) : ProcessCheckoutResult
    data class OrderAlreadyExist(val orderId: OrderId) : ProcessCheckoutResult
    data class CustomerDoesNotExist(val customerId: CustomerId) : ProcessCheckoutResult
    data class ProductsDoesNotExist(val productIds: Set<ProductId>) : ProcessCheckoutResult
}