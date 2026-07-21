package nu.westlin.eshop.common

@JvmInline
@Suppress("ValueClassParameterNaming")
value class Percentage(val fraction: Double) {

    init {
        require(fraction in 0.0..1.0) {
            "'fraction' have to be beteween 0.0 and 1.0 Got: $fraction"
        }
    }

    companion object {
        val ZERO = Percentage(0.0)
    }
}