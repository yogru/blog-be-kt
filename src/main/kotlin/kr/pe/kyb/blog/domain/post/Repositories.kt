package kr.pe.kyb.blog.domain.post

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import kr.pe.kyb.blog.infra.persistence.JpaBaseRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

interface PostRepository : JpaRepository<Post, UUID> {
    @Query(value = "SELECT p FROM Post p join fetch p.writer where p.id =:id")
    fun findByIdFetchUserValue(id: UUID): Post?
}

interface SeriesRepository : JpaRepository<Series, UUID> {}
interface PostUserValueRepository : JpaRepository<PostUserValue, UUID> {}
interface TagRepository : JpaRepository<Tag, String> {
    @Modifying
    @Query(
        value = "INSERT INTO tag(id, created_at, updated_at) VALUES(:tagName, :createdAt, :updatedAt) " +
                "ON DUPLICATE KEY UPDATE id=:tagName, updated_at=:updatedAt", nativeQuery = true
    )
    fun upsertTag(
        tagName: String,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    )
}

@Repository
class PostAggregateRepository(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val postUserValueRepository: PostUserValueRepository,
    private val seriesRepository: SeriesRepository,
) : JpaBaseRepository() {

    fun findTagById(tagName: String): Tag? {
        return tagRepository.findById(tagName).let {
            if (it.isEmpty) null else it.get()
        }
    }

    fun findPostById(id: UUID): Post? {
        return postRepository.findById(id).let {
            if (it.isEmpty) null else it.get()
        }
    }

    fun findByIdFetchUserValue(id: UUID): Post? {
        return postRepository.findByIdFetchUserValue(id)
    }

    fun upsertTag(tagName: String) {
        tagRepository.upsertTag(tagName)
    }

    fun findAllTag(): List<Tag> {
        return tagRepository.findAll()
    }

    fun findUserValueById(id: UUID): PostUserValue? {
        return postUserValueRepository.findById(id)
            .let { if (it.isEmpty) null else it.get() }
    }
}