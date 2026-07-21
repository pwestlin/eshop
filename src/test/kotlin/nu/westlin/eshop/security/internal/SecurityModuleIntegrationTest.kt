package nu.westlin.eshop.security.internal

import nu.westlin.eshop.common.example
import nu.westlin.eshop.customer.NewCustomerRegisteredEvent
import nu.westlin.eshop.test.ModulithIntegrationTest
import nu.westlin.eshop.test.SharedTestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.Scenario

@ModulithIntegrationTest
@Import(SharedTestcontainersConfiguration::class)
class SecurityModuleIntegrationTest @Autowired constructor(private val userRepository: UserRepository) {

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