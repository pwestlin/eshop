package nu.westlin.eshop.security.internal

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
class SecurityConfiguration {

    @Suppress("DEPRECATION")
    @Bean
    fun passwordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { csrf -> csrf.disable() } // Inaktivera CSRF för enklare API-testning i labbmiljö
        .authorizeHttpRequests { auth ->
            auth.anyRequest().authenticated()
        }
        .httpBasic(Customizer.withDefaults()) // Aktiverar Basic Auth
        .build()
}