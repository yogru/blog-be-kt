package kr.pe.kyb.blog.infra.persistence

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Component

@Component
class EntityManagerWrapper {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun <T : JPABaseEntity> make(entity: T): T {
        em.persist(entity)
        return entity
    }

    fun <T : JPABaseEntity> update(entity: T): T {
        em.merge(entity)
        return entity
    }
}