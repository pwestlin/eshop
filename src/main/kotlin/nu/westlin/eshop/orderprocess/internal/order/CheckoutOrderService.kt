package nu.westlin.eshop.orderprocess.internal.order

import nu.westlin.eshop.catalog.AllProductsExistResponse
import nu.westlin.eshop.catalog.CatalogFacade
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.common.OrderId
import nu.westlin.eshop.common.ProductId
import nu.westlin.eshop.customer.CustomerFacade
import nu.westlin.eshop.order.OrderCreationCommand
import nu.westlin.eshop.order.OrderDiscountInput
import nu.westlin.eshop.order.OrderFacade
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutOrderService(
    private val orderFacade: OrderFacade,
    private val customerFacade: CustomerFacade,
    private val catalogFacade: CatalogFacade,
) {

    @Transactional
    @Suppress("ReturnCount")
    fun processCheckout(
        orderId: OrderId,
        customerId: CustomerId,
        // TODO pwestlin: Lite fult att använda CheckoutRequest.Item men jag orkar inte skapa en ny typ just nu
        items: Set<CheckoutRequest.Item>,
    ): CheckoutResult {
        if (!orderFacade.isOrderIdUnique(orderId)) {
            return CheckoutResult.OrderAlreadyExist(orderId)
        }

        if (!customerFacade.customerExists(customerId)) {
            return CheckoutResult.CustomerDoesNotExist(customerId)
        }

        catalogFacade.allProductsExist(items.map { ProductId(it.productId) }).let { response ->
            when (response) {
                AllProductsExistResponse.AllExist -> Unit

                is AllProductsExistResponse.MissingProducts -> return CheckoutResult.ProductsDoesNotExist(
                    response.productIds,
                )
            }
        }

        val customerDiscount = customerFacade.getActiveDiscountFor(customerId)
        val orderCreationCommand = OrderCreationCommand(
            orderId = orderId,
            customerId = customerId,
            items = items.toOrderCreationCommandItems(),
            discount = OrderDiscountInput(
                code = customerDiscount.tier,
                type = OrderDiscountInput.DiscountType.PERCENTAGE,
                value = (customerDiscount.rate.fraction * 100).toInt(),
            ),
        )
        orderFacade.createOrder(orderCreationCommand)

        return CheckoutResult.Success
    }
}

private fun Set<CheckoutRequest.Item>.toOrderCreationCommandItems(): Set<OrderCreationCommand.Item> = this.map { item ->
    OrderCreationCommand.Item(
        productId = ProductId(item.productId),
        quantity = item.quantity,
        price = item.price,
    )
}.toSet()

sealed interface CheckoutResult {

    data object Success : CheckoutResult
    data class OrderAlreadyExist(val orderId: OrderId) : CheckoutResult
    data class CustomerDoesNotExist(val customerId: CustomerId) : CheckoutResult
    data class ProductsDoesNotExist(val productIds: List<ProductId>) : CheckoutResult
}