package kr.pe.kyb.blog.domain.post

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*


interface PostRepository : JpaRepository<Post, UUID> {


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

    @Query(value = "SELECT t FROM Tag t ")
    fun findAllTag(): Set<Tag>

    @Query(value = "SELECT t FROM Tag t WHERE t.id =:id")
    fun findTagById(id: String): Tag?

    @Modifying
    @Query(value = "DELETE FROM Tag t WHERE t.id =:id")
    fun deleteTagById(id: String)

    @Query(value = "SELECT u FROM PostUserValue u WHERE u.id =:id ")
    fun findOneUserValueById(id: UUID): PostUserValue?


    @Query(value = "SELECT p FROM Post p join fetch p.writer where p.id =:id")
    fun findByIdFetchUserValue(id: UUID):Post?
}
