package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.apache.commons.lang3.RandomStringUtils

fun Customer.Companion.example(
    id: CustomerId = CustomerId.generate(),
    name: String = RandomStringUtils.secure().nextAlphabetic(10),
    email: Email = EmailGenerator.generateRandomEmail(),
): Customer = Customer(
    id = id,
    name = name,
    email = email,
)

object EmailGenerator {

    private val adjectives = listOf(
        "agile", "binary", "clever", "dashing", "elegant", "focused",
        "glowing", "happy", "insane", "joyful", "keen", "logical",
        "magical", "neat", "optimistic", "proud", "quick", "robust",
        "sharp", "thoughtful",
    )

    private val nouns = listOf(
        "ada", "alan", "bell", "curie", "dart", "elixir",
        "fermat", "gauss", "hopper", "io", "java", "kernel",
        "lambda", "mips", "node", "oracle", "python", "qubit",
        "rust", "stack",
    )

    private val domains = listOf("se", "com", "nu")

    fun generateRandomEmail(): Email {
        val adjective = adjectives.random()
        val noun = nouns.random()
        val domain = domains.random()

        return Email("$adjective.$noun@example.$domain")
    }
}