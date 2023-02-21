package kr.pe.kyb.blog.infra.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
open abstract class QueryDslBase<T : Any> {
    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    protected val jpaQueryFactory: JPAQueryFactory
        get() = JPAQueryFactory(entityManager)


    fun add(model: T): T = this.entityManager.merge(model)
}