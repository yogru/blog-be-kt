package kr.pe.kyb.blog.infra.persistence

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository


@Repository
class JpaBaseRepository {

    @PersistenceContext
    protected lateinit var em: EntityManager

    fun <T : JPABaseEntity> persist(entity: T): T {
        em.persist(entity)
        return entity
    }

    fun <T : JPABaseEntity> merge(entity: T): T {
        return em.merge(entity)
    }

    fun <T : JPABaseEntity> remove(entity: T) {
        em.remove(entity)
    }
}