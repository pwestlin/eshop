package nu.westlin.eshop.config

import org.springframework.modulith.ApplicationModule

@ApplicationModule(allowedDependencies = ["common"])
object ConfigModule