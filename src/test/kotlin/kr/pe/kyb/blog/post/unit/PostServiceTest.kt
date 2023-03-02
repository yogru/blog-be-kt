package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.mock.MyTest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@MyTest
class PostServiceTest {


    @Autowired
    lateinit var postTestService: PostService



    @Test
    @Transactional
    fun createPost() {
        val ret1 = postTestService.createPost(CreatePostDto(title = "title", body = "body", tags = listOf("All")))
        Assertions.assertNotNull(ret1)
        val ret2 = postTestService.createPost(CreatePostDto(title = "title2", body = "body2", tags = listOf("All")))
        Assertions.assertNotNull(ret2)
        Assertions.assertEquals(ret1.writerId, ret2.writerId)
        Assertions.assertEquals(ret1.writerName, ret2.writerName)
    }

    @Test
    @Transactional
    fun tagCurd() {
        val deletedPromised = "리액트"
        val tagNames = listOf("All", deletedPromised, "코틀린", "데이터베이스")
        for (tagName in tagNames) {
            postTestService.upsertTag(tagName)
        }
        val foundTags = postTestService.getAllTags()
        Assertions.assertEquals(foundTags.size, 4)
        Assertions.assertEquals(foundTags.sorted(), tagNames.sorted())

        postTestService.deleteTag(deletedPromised)
        Assertions.assertFalse(postTestService.getAllTags().contains(deletedPromised))

        Assertions.assertThrows(UnremovableTagException::class.java) {
            postTestService.deleteTag("All")
        }
        Assertions.assertThrows(NotFoundTag::class.java) {
            postTestService.deleteTag(deletedPromised)
        }
    }

}