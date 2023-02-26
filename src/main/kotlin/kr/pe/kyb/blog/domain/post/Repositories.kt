package kr.pe.kyb.blog.domain.post

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*


interface PostRepository : JpaRepository<Post, UUID> {
}

interface TagRepository : JpaRepository<Tag, String> {


    @Transactional
    @Modifying
    @Query(
        value = "INSERT INTO tag(id, created_at, updated_at) VALUES(:tagName, :createdAt, :updatedAt) " +
                "ON DUPLICATE KEY UPDATE id=:tagName, updated_at=:updatedAt", nativeQuery = true
    )
    fun upsert(
        tagName: String,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    )

}