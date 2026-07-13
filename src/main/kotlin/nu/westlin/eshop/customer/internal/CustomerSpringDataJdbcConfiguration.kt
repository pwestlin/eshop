package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.common.CustomerId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.*

@WritingConverter
class CustomerIdWritingConverter : Converter<CustomerId, UUID> {
    override fun convert(source: CustomerId): UUID = source.value
}

@ReadingConverter
class CustomerIdReadingConverter : Converter<UUID, CustomerId> {
    override fun convert(source: UUID): CustomerId = CustomerId(source)
}

@Configuration
class CustomerSpringDataJdbcConfiguration {

    @Bean
    fun customerConverters(): List<Converter<*, *>> = listOf(
        CustomerIdWritingConverter(),
        CustomerIdReadingConverter(),
    )
}