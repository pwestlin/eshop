package nu.westlin.eshop.catalog

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON])
object CatalogModule