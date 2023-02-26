package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.CreatePostDto
import kr.pe.kyb.blog.domain.post.NotFoundTag
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.domain.post.UnremovableTagException
import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.mock.data.createTestUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional(readOnly = true)
@SpringBootTest
class PostServiceTest {

    @Autowired
    lateinit var postService: PostService

    @Autowired
    lateinit var joinService: JoinService

    @Test
    @Transactional
    fun createPost() {
        val userId: UUID = createTestUser(joinService)
        var ret = postService
            .createPost(CreatePostDto(userId = userId, title = "title", body = "body", listOf("All")))
        Assertions.assertNotNull(ret)
    }


    @Test
    @Transactional
    fun tagCurd() {
        var deletedPromised = "리액트"
        var tagNames = listOf("All", deletedPromised, "코틀린", "데이터베이스")
        for (tagName in tagNames) {
            postService.upsertTag(tagName)
        }
        val foundTags = postService.getAllTags()
        Assertions.assertEquals(foundTags.size, 4)
        Assertions.assertEquals(foundTags.sorted(), tagNames.sorted())

        postService.deleteTag(deletedPromised)

        Assertions.assertFalse(postService.getAllTags().contains(deletedPromised))
        Assertions.assertThrows(UnremovableTagException::class.java) {
            postService.deleteTag("All")
        }
        Assertions.assertThrows(NotFoundTag::class.java) {
            postService.deleteTag(deletedPromised)
        }
        
    }


}