package nu.westlin.eshop

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import org.springframework.stereotype.Service
import java.io.File

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

    @Test
    fun `verify that public API has no @Service`() {
        // 1. Hämta alla klassnamn som Spring Modulith betraktar som publika
        val publicModuleClassNames = modules.flatMap { module ->
            module.namedInterfaces.flatMap { namedInterface ->
                namedInterface.map { javaClass -> javaClass.name }
            }
        }.toSet()

        // 2. Skapa ett eget DescribedPredicate. Detta slipper helt ArchUnits interna klass-hierarkier.
        val belongToPublicApi = object : DescribedPredicate<JavaClass>("reside in a public Modulith API") {
            override fun test(javaClass: JavaClass): Boolean = javaClass.name in publicModuleClassNames
        }

        // 3. Skicka predikatet direkt till .that(predicate)
        val publicApiRule = noClasses()
            .that(belongToPublicApi)
            .should().haveSimpleNameEndingWith("Service")
            .orShould().beAnnotatedWith(Service::class.java)
            .`as`(
                "The modules public API can't contains classes with names ending with 'Service' or that is  annotated with @Service",
            )

        // 4. Kör analysen
        val javaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackagesOf(Application::class.java)

        publicApiRule.check(javaClasses)
    }

    @Test
    fun verifyOnlyInternalSubpackageIsAllowed() {
        // Hämta alla unika baspaket för modulerna (t.ex. "nu.westlin.eshop.order")
        val moduleBasePackages = modules.map { it.basePackage.name }.toSet()

        // 1. Predikat för att hitta klasser som faktiskt tillhör en Modulith-modul
        // (Vi ignorerar klasser i applikationens rotpaket, t.ex. själva Application.kotlin)
        val resideInsideAModule = object : DescribedPredicate<JavaClass>("reside inside a Modulith module") {
            override fun test(javaClass: JavaClass): Boolean {
                val pkg = javaClass.packageName
                return moduleBasePackages.any { base -> pkg == base || pkg.startsWith("$base.") }
            }
        }

        // 2. Villkor som validerar att underpaket strikt heter "internal" eller underkataloger till det
        val onlyHaveInternalAsSubpackage =
            object : ArchCondition<JavaClass>("only have 'internal' as direct subpackage under the module root") {
                override fun check(javaClass: JavaClass, events: ConditionEvents) {
                    val pkg = javaClass.packageName

                    // Hitta den specifika modulroten för denna klass
                    val base = moduleBasePackages.first { base -> pkg == base || pkg.startsWith("$base.") }

                    // Om klassen ligger direkt i modulroten (det publika API:et) är det helt ok
                    if (pkg == base) return

                    // Plocka ut paketstigen efter modulroten (t.ex. "infrastructure" eller "internal.db")
                    val relativePackage = pkg.removePrefix("$base.")

                    // Validera att paketet antingen är exakt "internal" eller börjar med "internal."
                    val isAllowed = relativePackage == "internal" || relativePackage.startsWith("internal.")

                    if (!isAllowed) {
                        val message = "The class ${javaClass.name} is located in the package '$pkg'. " +
                            "The only allowed subpackage directly under the module root ($base) is 'internal'."
                        events.add(SimpleConditionEvent.violated(javaClass, message))
                    }
                }
            }

        // 3. Bygg och kör regeln
        val rule = classes()
            .that(resideInsideAModule)
            .should(onlyHaveInternalAsSubpackage)

        val javaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackagesOf(Application::class.java)

        rule.check(javaClasses)
    }

    @Test
    fun verifyNoEmptyPackages() {
        val sourceRoot = File("src/main/kotlin")

        // Om katalogen inte finns (t.ex. i en ren resursmodul), avbryt i tid
        if (!sourceRoot.exists()) return

        // Leta efter kataloger som är helt tomma
        val emptyPackages = sourceRoot.walkBottomUp()
            .filter { it.isDirectory }
            .filter { dir -> dir.list()?.isEmpty() ?: false }
            .map { dir ->
                // Gör om filsökvägen till ett läsbart paketnamn (t.ex. nu.westlin.inventory.foo)
                dir.relativeTo(sourceRoot).path.replace(File.separator, ".")
            }
            .toList()

        if (emptyPackages.isNotEmpty()) {
            val affectedPackages = emptyPackages.joinToString("\n") { "- $it" }
            Assertions.fail<Nothing>(
                "The following packages are empty and should be removed from the file system:\n$affectedPackages",
            )
        }
    }
}