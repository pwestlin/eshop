package nu.westlin.eshop.test

import kotlin.random.Random

object EmailGenerator {

    private val firstNames: List<String> = listOf(
        "adoring", "vibrant", "dreamy", "elastic", "focused",
        "quirky", "clever", "brave", "epic", "silent",
        "hungry", "goofy", "elated", "frosty", "modest",
        "wild", "zen", "fancy", "jolly", "sleepy",
    )

    private val lastNames: List<String> = listOf(
        "einstein", "lovelace", "curie", "hopper", "turing",
        "tesla", "darwin", "hawking", "babbage", "pasteur",
        "galileo", "newton", "mendel", "franklin", "feynman",
        "bohr", "copernicus", "planck", "socrates", "hertz",
    )

    private val tlds: List<String> = listOf("com", "se", "nu")

    /**
     * Generates a ramdom email with formatet: firstName.lastName@domain.foo
     */
    fun generate(): String {
        val firstName = firstNames.random()
        val lastName = lastNames.random()

        // Genererar ett tal mellan 0 och 999 och fyller ut med nollor till 3 tecken (t.ex. 042)
        val nnn = Random.nextInt(1000).toString().padStart(3, '0')
        val foo = tlds.random()

        return "$firstName.$lastName$nnn@domain.$foo"
    }
}