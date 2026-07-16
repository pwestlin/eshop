package nu.westlin.eshop.security.internal

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JdbcUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val appUser = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User with user name '$username' was not found.")

        val authorities = appUser.roles.split(",")
            .map { role -> SimpleGrantedAuthority("ROLE_${role.trim()}") }

        return User.builder()
            .username(appUser.username)
            .password(appUser.password)
            .authorities(authorities)
            .build()
    }
}