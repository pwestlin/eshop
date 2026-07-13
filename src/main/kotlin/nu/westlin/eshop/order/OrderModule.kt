package nu.westlin.eshop.order

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON, Modules.CUSTOMER])
object OrderModule