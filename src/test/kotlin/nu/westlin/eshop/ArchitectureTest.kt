package nu.westlin.eshop

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ArchitectureTest {

    private val modules = ApplicationModules.of(Application::class.java)

    @Test
    fun `verify modular structure`() {
        // Det här testet verifierar att:
        // 1. Inga cykliska beroenden finns mellan moduler.
        // 2. Moduler endast beror på publika API:er i andra moduler (inte interna paket).
        modules.verify()
    }

    @Disabled
    @Test
    fun `create module documentation`() {
        // Valfritt men extremt trevligt: Skapar automatiskt en
        // komponent-dokumentation (PlantUML/C4) i din build-katalog.
        Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
    }
}