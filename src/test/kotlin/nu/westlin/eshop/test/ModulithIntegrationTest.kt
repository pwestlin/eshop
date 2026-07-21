package nu.westlin.eshop.test

import nu.westlin.eshop.config.CentralSpringDataJdbcConfiguration
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "spring.datasource.hikari.connection-timeout=2000",
        "spring.datasource.hikari.validation-timeout=1000",
    ],
)
@ApplicationModuleTest
@Import(SharedTestcontainersConfiguration::class, CentralSpringDataJdbcConfiguration::class)
annotation class ModulithIntegrationTest

@ModulithIntegrationTest
@AutoConfigureRestTestClient
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "customer", roles = ["CUSTOMER"])
annotation class ModulithWebIntegrationTest