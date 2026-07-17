package nu.westlin.eshop.security

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON])
object SecurityModule