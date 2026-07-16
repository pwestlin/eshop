package nu.westlin.eshop

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ArchitectureTest {

    private val modules = ApplicationModules.of(Application::class.java)

    @Test
    fun `verify modular structure`() {
        modules.verify()
    }

    @Disabled
    @Test
    fun `print module information`() {
        println(modules)
    }

    @Disabled
    @Test
    fun `create module documentation`() {
        Documenter(modules)
            .writeDocumentation()
    }
}