package nu.westlin.eshop.payment

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON])
object PaymentModule