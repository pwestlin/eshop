package nu.westlin.eshop.customer.internal.registernewcustomer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.customer.NewCustomerRegisteredEvent
import nu.westlin.eshop.customer.internal.Customer
import nu.westlin.eshop.customer.internal.CustomerRepository
import nu.westlin.eshop.customer.internal.Email
import nu.westlin.eshop.customer.internal.SpringDataCustomerRepository
import nu.westlin.eshop.customer.internal.example
import nu.westlin.eshop.test.EmailGenerator
import nu.westlin.eshop.test.ModulithWebIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.modulith.test.PublishedEvents
import org.springframework.modulith.test.Scenario
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody

@ModulithWebIntegrationTest
class RegisterNewCustomerIntegrationTest(
    private val client: RestTestClient,
    private val customerRepository: CustomerRepository,
    private val springDataCustomerRepository: SpringDataCustomerRepository,
) {

    @Test
    fun `register a new customer`(scenario: Scenario) {
        val request = NewCustomerRequest(
            name = "Foo Bar",
            email = EmailGenerator.generate(),
            username = "foobar",
            password = "aarfoo",
        )

        var generatedCustomerId: CustomerId = CustomerId.generate()
        scenario.stimulate {
            client
                .post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody<NewCustomerResponse>()
                .value { response ->
                    requireNotNull(response)
                    generatedCustomerId = CustomerId(response.customerId)
                }
        }.andWaitForEventOfType(NewCustomerRegisteredEvent::class.java)
            .matching { event ->
                event == NewCustomerRegisteredEvent(
                    customerId = generatedCustomerId,
                    name = request.name,
                    email = request.email,
                    username = request.username,
                    password = request.password,
                )
            }
            .toArrive()

        assertThat(customerRepository.getById(generatedCustomerId)).isEqualTo(
            Customer(
                id = generatedCustomerId,
                name = request.name,
                email = Email(request.email),
            ),
        )
    }

    @Test
    fun `register a customer that already exists (same email) should return conflict and no event should be pulished and notning save in the db`(
        publishedEvents: PublishedEvents,
    ) {
        val customer = Customer.example()
        customerRepository.insert(customer)

        val request = NewCustomerRequest(
            name = "Koma Klasse",
            email = customer.email.value,
            username = "foobar",
            password = "aarfoo",
        )
        client
            .post()
            .uri("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectHeader().doesNotExist(HttpHeaders.CONTENT_TYPE)
            .expectBody().isEmpty

        assertThat(publishedEvents.ofType(NewCustomerRegisteredEvent::class.java)).isEmpty()

        assertThat(springDataCustomerRepository.findByEmail(customer.email)).isEqualTo(customer)
    }
}