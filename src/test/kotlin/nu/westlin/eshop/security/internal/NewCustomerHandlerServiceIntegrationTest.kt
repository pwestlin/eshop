package nu.westlin.eshop.security.internal

import nu.westlin.eshop.common.NewCustomerRegisteredEvent
import nu.westlin.eshop.common.example
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario
import org.springframework.test.context.TestPropertySource

// TODO pwestlin: Skapa en annotering som gör mycket av dessa nedan
// When you run the test with Gradle you get 30 sec timeout after completed test suite and the below is to fix that...
@TestPropertySource(
    properties = [
        "spring.datasource.hikari.connection-timeout=2000",
        "spring.datasource.hikari.validation-timeout=1000",
    ],
)
@ApplicationModuleTest
@Import(SharedTestcontainersConfiguration::class)
class NewCustomerHandlerServiceIntegrationTest @Autowired constructor(private val userRepository: UserRepository) {

    @Test
    fun `handle NewCustomerRegisteredEvent`(scenario: Scenario) {
        val event = NewCustomerRegisteredEvent.example()
        scenario.publish(event)
            .andWaitForStateChange {
                val appUser = userRepository.findByUsername(event.username)
                checkNotNull(appUser)
                appUser
            }
            .andVerify { appUser ->
                assertThat(appUser).isEqualTo(
                    AppUser(
                        username = event.username,
                        password = event.password,
                        roles = "CUSTOMER",
                    ),
                )
            }
    }
}