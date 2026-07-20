package nu.westlin.eshop.customer.internal.registernewcustomer

import nu.westlin.eshop.common.CustomerId
import nu.westlin.eshop.customer.internal.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/customers")
class RegisterNewCustomerController(
    private val customerRepository: CustomerRepository,
    private val registerNewCustomerService: RegisterNewCustomerService,
) {

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerNewCustomer(@RequestBody request: NewCustomerRequest): ResponseEntity<Any> =
        when (val result = registerNewCustomerService.register(request)) {
            is NewCustomerRequestResult.Ok -> {
                val location: URI = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(result.customerId.value)
                    .toUri()
                ResponseEntity.created(location).body(NewCustomerResponse(customerId = result.customerId.value))
            }

            NewCustomerRequestResult.Duplicate -> {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        }

    // TODO pwestlin: Borde nog ligga i en annan klass. Behövs den ens?
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCustomer(@PathVariable id: CustomerId): ResponseEntity<GetCustomerResponse> {
        val customer = customerRepository.findByIdOrNull(id)

        return if (customer != null) {
            ResponseEntity.ok(
                GetCustomerResponse(
                    id = customer.id.value,
                    name = customer.name,
                    email = customer.email.value,
                ),
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class GetCustomerResponse(val id: UUID, val name: String, val email: String)

data class NewCustomerRequest(val name: String, val email: String, val username: String, val password: String)

data class NewCustomerResponse(val customerId: UUID)