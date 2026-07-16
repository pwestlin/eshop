package nu.westlin.eshop.inventory

import nu.westlin.eshop.Modules
import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = [Modules.COMMON])
object InventoryModule