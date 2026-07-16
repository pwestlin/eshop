package nu.westlin.eshop.customer

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON])
object CustomerModule