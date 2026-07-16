package nu.westlin.eshop.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class CentralSpringDataJdbcConfiguration(private val allCustomConverters: List<List<Converter<*, *>>>) :
    AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> = allCustomConverters.flatten()
}