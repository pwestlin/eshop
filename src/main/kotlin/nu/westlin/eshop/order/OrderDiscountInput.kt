package nu.westlin.eshop.order

data class OrderDiscountInput(val code: String, val type: DiscountType, val value: Int) {

    enum class DiscountType {
        PERCENTAGE
    }
}
