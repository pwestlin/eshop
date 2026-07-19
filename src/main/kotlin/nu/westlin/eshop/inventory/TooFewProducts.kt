package nu.westlin.eshop.inventory

import nu.westlin.eshop.common.ProductId

data class TooFewProducts(val productId: ProductId, val orderQuantity: Int, val inventoryQuantity: Int)
