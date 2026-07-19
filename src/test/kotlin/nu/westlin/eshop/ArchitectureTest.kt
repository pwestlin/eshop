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

    // TODO pwestlin: Skapa regel som kontrollerar att inga klasser som ligger i det publika API:et för en modul heter *Service och/eller är annoterad med @Service.
    //  I så fall ska de ska heta *Facade och vara annoterade med @Component.
}