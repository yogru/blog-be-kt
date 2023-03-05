package kr.pe.kyb.blog.domain.user

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

interface RoleRepository : JpaRepository<Role, RoleEum> {

    @Query("SELECT r FROM Role r where r.id in :ids ")
    fun findInIds(ids: List<RoleEum>): List<Role>
}

interface UserRepository : JpaRepository<UserEntity, UUID> {

    @Query(
        "SELECT u FROM UserEntity u " +
                " left join fetch u.userRoles userRoles " +
                " left join fetch userRoles.role" +
                " where u.account = :account"
    )
    fun findOneByAccount(account: String): UserEntity?

    @Query(
        "SELECT u FROM UserEntity u " +
                " left join fetch u.userRoles userRoles " +
                " left join fetch userRoles.role" +
                " where u.id = :id"
    )
    fun findOneById(id: UUID): UserEntity?
}

@Repository
class UserAggregateRepository(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun findOneByAccount(account: String): UserEntity? {
        return userRepository.findOneByAccount(account)
    }
    fun findOneById(id: UUID): UserEntity? {
        return userRepository.findOneById(id)
    }

    fun findRoleInId(ids: List<RoleEum>): List<Role> {
        if (ids.isNullOrEmpty()) return listOf()
        return roleRepository.findInIds(ids)
    }
}