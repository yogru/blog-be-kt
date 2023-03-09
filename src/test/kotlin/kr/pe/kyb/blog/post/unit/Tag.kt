package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.NotFoundTag
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.domain.post.UnremovableTagException
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@MyTest
@DisplayName("unit: Tag")
class Tag {


    @Autowired
    lateinit var postTestService: PostService


    @Test
    @Transactional
    fun curd() {
        val deletedPromised = "리액트"
        val tagNames = listOf(deletedPromised, "코틀린", "데이터베이스")
        for (tagName in tagNames) {
            postTestService.upsertTag(tagName)
        }
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