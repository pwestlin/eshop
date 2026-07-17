package nu.westlin.eshop.customer.internal.registernewcustomer

import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.unmockkObject
import io.mockk.verify
import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.customer.internal.Customer
import nu.westlin.eshop.customer.internal.CustomerRepository
import nu.westlin.eshop.customer.internal.Email
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody

@WebMvcTest(RegisterNewCustomerController::class)
@AutoConfigureRestTestClient
@AutoConfigureMockMvc(addFilters = false)
class RegisterNewCustomerControllerTest(@Autowired private val client: RestTestClient) {

    @MockkBean
    private lateinit var customerRepository: CustomerRepository

    @MockkBean
    private lateinit var registerNewCustomerService: RegisterNewCustomerService

    @AfterEach
    fun tearDown() {
        unmockkObject(CustomerId.Companion)
    }

    @Test
    fun `register a new customer`() {
        val request = NewCustomerRequest(
            name = "Charlie Walker",
            email = "ila.blackwell@example.com",
            username = "theuser",
            password = "thepassword",
        )
        val customerId = CustomerId.generate()
        every {
            registerNewCustomerService.register(request)
        } returns NewCustomerRequestResult.Ok(customerId)

        val response = NewCustomerResponse(customerId.value)
        client
            .post()
            .uri("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("http://localhost/customers/${response.customerId}")
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<NewCustomerResponse>()
            .value { response ->
                assertThat(response).isEqualTo(response)
            }

        verify {
            registerNewCustomerService.register(request)
        }
        confirmVerified(registerNewCustomerService, customerRepository)
    }

    @Test
    fun `register a new customer with an email that is already registered to another customer`() {
        val request = NewCustomerRequest(
            name = "Charlie Walker",
            email = "ila.blackwell@example.com",
            username = "theuser",
            password = "thepassword",
        )
        every {
            registerNewCustomerService.register(request)
        } returns NewCustomerRequestResult.Duplicate

        client
            .post()
            .uri("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectHeader().doesNotExist(HttpHeaders.LOCATION)
            .expectHeader().doesNotExist(HttpHeaders.CONTENT_TYPE)
            .expectBody().isEmpty()

        verify {
            registerNewCustomerService.register(request)
        }
        confirmVerified(registerNewCustomerService, customerRepository)
    }

    @Test
    fun `get customer that exists by id`() {
        val customerId = CustomerId.generate()
        val customer = Customer(id = customerId, name = "Foo", email = Email("foo@bar.nu"))
        every { customerRepository.findByIdOrNull(customerId) } returns customer

        client
            .get()
            .uri("/customers/{id}", customerId.value)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<GetCustomerResponse>()
            .value {
                assertThat(it).isEqualTo(
                    GetCustomerResponse(
                        id = customer.id.value,
                        name = customer.name,
                        email = customer.email.value,
                    ),
                )
            }

        verify { customerRepository.findByIdOrNull(customerId) }
        confirmVerified(customerRepository)
    }

    @Test
    fun `get customer that does not exist by id`() {
        val customerId = CustomerId.generate()
        every { customerRepository.findByIdOrNull(customerId) } returns null

        client
            .get()
            .uri("/customers/{id}", customerId.value)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().doesNotExist(HttpHeaders.CONTENT_TYPE)
            .expectBody().isEmpty

        verify { customerRepository.findByIdOrNull(customerId) }
        confirmVerified(customerRepository)
    }
}