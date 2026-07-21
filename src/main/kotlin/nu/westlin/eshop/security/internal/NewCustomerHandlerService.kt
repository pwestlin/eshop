package nu.westlin.eshop.security.internal

import nu.westlin.eshop.customer.NewCustomerRegisteredEvent
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class NewCustomerHandlerService(private val userRepository: UserRepository) {

    @ApplicationModuleListener
    fun handleNewCustomerRegisteredEvent(event: NewCustomerRegisteredEvent) {
        val appuser = AppUser(username = event.username, password = event.password, roles = "CUSTOMER")
        userRepository.insert(appuser)
    }
}