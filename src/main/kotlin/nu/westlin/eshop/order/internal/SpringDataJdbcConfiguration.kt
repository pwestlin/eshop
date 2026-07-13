package nu.westlin.eshop.order.internal

import nu.westlin.eshop.common.OrderIdReadingConverter
import nu.westlin.eshop.common.OrderIdWritingConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class SpringDataJdbcConfiguration : AbstractJdbcConfiguration() {
    override fun userConverters(): List<*> = listOf(OrderIdWritingConverter(), OrderIdReadingConverter())
}