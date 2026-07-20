package nu.westlin.eshop.customer

import nu.westlin.eshop.common.Percentage

data class CustomerDiscount(
    val tier: String, // T.ex. "GOLD", "SILVER", "BRONZE" eller "NEW"
    val rate: Percentage,
)

