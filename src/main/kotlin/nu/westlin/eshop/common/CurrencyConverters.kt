package nu.westlin.eshop.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.*

@WritingConverter
class CurrencyToStringConverter : Converter<Currency, String> {
    override fun convert(source: Currency): String = source.currencyCode
}

@ReadingConverter
class StringToCurrencyConverter : Converter<String, Currency> {
    override fun convert(source: String): Currency = Currency.getInstance(source)
}

@Configuration
class CurrencySpringDataJdbcConfiguration {

    @Bean
    fun currencyConverters(): List<Converter<*, *>> = listOf(
        CurrencyToStringConverter(),
        StringToCurrencyConverter(),
    )
}