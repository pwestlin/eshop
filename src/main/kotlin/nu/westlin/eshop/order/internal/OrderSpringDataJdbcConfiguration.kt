package nu.westlin.eshop.order.internal

import nu.westlin.eshop.common.OrderId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.*

@WritingConverter
class OrderIdWritingConverter : Converter<OrderId, UUID> {
    override fun convert(source: OrderId): UUID = source.value
}

@ReadingConverter
class OrderIdReadingConverter : Converter<UUID, OrderId> {
    override fun convert(source: UUID): OrderId = OrderId(source)
}

@Configuration
class OrderSpringDataJdbcConfiguration {

    @Bean
    fun orderConverters(): List<Converter<*, *>> = listOf(
        OrderIdWritingConverter(),
        OrderIdReadingConverter(),
    )
}