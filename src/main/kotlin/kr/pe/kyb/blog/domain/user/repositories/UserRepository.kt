package kr.pe.kyb.blog.domain.user.repositories

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.domain.user.models.UserEntity
import kr.pe.kyb.blog.infra.persistence.QueryDslBase
import org.springframework.stereotype.Repository

@Repository
class UserRepository : QueryDslBase<UserEntity>() {
}