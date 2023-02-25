package kr.pe.kyb.blog.domain.post

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostRepository : JpaRepository<Post, UUID> {
}

