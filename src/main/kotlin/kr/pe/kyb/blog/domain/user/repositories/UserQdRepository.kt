package kr.pe.kyb.blog.domain.user.repositories

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository


@Repository
class UserQdRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

}