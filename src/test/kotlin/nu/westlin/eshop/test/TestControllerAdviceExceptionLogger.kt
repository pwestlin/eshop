package nu.westlin.eshop.test

import nu.westlin.eshop.common.logger
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@TestConfiguration(proxyBeanMethods = false)
class TestLoggingAutoConfiguration {

    @Bean
    fun testControllerAdviceExceptionLogger(): TestControllerAdviceExceptionLogger =
        TestControllerAdviceExceptionLogger()
}

@ControllerAdvice
class TestControllerAdviceExceptionLogger {

    private val log = logger()

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<String> {
        // Detta tvingar fram stacktracen i dina testloggar direkt när det smäller!
        log.error("Test request failed with exception:", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.message)
    }
}