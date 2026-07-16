package nu.westlin.eshop.security.internal

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<AppUser, String> {
    fun findByUsername(username: String): AppUser?
}
