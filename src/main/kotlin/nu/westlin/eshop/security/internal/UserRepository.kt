package nu.westlin.eshop.security.internal

import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataUserRepository : CrudRepository<AppUser, String> {
    fun findByUsername(username: String): AppUser?
}

@Repository
class UserRepository(
    private val springDataUserRepository: SpringDataUserRepository,
    private val entityTemplate: JdbcAggregateTemplate,
) {
    fun findByUsername(username: String): AppUser? = springDataUserRepository.findByUsername(username)

    fun insert(appUser: AppUser) {
        entityTemplate.insert(appUser)
    }
}
