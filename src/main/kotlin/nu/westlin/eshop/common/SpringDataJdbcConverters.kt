package nu.westlin.eshop.common

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
