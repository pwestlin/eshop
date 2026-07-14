package nu.westlin.eshop.catalog.internal

import nu.westlin.eshop.common.ProductId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class ProductIdWritingConverter : Converter<ProductId, Int> {
    override fun convert(source: ProductId): Int = source.value
}

@ReadingConverter
class ProductIdReadingConverter : Converter<Int, ProductId> {
    override fun convert(source: Int): ProductId = ProductId(source)
}

@Configuration
class ProductSpringDataJdbcConfiguration {

    @Bean
    fun productConverters(): List<Converter<*, *>> = listOf(
        ProductIdWritingConverter(),
        ProductIdReadingConverter(),
    )
}