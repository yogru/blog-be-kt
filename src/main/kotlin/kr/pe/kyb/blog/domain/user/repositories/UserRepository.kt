package kr.pe.kyb.blog.domain.user.repositories

import kr.pe.kyb.blog.domain.user.models.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findOneByAccount(account: String): UserEntity?
}