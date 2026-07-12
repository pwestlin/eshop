package nu.westlin.eshop.common

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import java.util.*

// TODO pwestlin: Testa
@WritingConverter
class OrderIdWritingConverter : Converter<OrderId, UUID> {
    override fun convert(source: OrderId): UUID = source.value
}

// TODO pwestlin: Testa
@ReadingConverter
class OrderIdReadingConverter : Converter<UUID, OrderId> {
    override fun convert(source: UUID): OrderId = OrderId(source)
}

@Configuration
class JdbcConfiguration : AbstractJdbcConfiguration() {
    override fun userConverters(): List<*> = listOf(OrderIdWritingConverter(), OrderIdReadingConverter())
}