package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.PostService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@SpringBootTest
class PostServiceTest {

    @Autowired
    lateinit var postService: PostService

    @Test
    @Transactional
    fun createPost() {

    }
}