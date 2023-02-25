package kr.pe.kyb.blog.domain.user

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pe.kyb.blog.domain.user.models.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findOneByAccount(account: String): UserEntity?
}

@Repository
class UserQdRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

}