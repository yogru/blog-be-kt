package kr.pe.kyb.blog.domain.post

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*


interface PostRepository : JpaRepository<Post, UUID> {


    @Transactional
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

    @Query(value = "DELETE FROM Tag t WHERE t.id =:id")
    fun deleteTagById(id: String)

    @Query(value = "SELECT u FROM PostUserValue u WHERE u.id = :id ")
    fun findUserValueById(id: UUID): PostUserValue?
}
