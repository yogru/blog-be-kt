package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.mock.MyTest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@MyTest
@DisplayName("unit: Post")
class PostServiceTest {

    @Autowired
    lateinit var postTestService: PostService


    @Test
    @Transactional
    fun postCurd() {
        val post1 = postTestService.createPost(CreatePostDto(title = "title", body = "body", tags = listOf("All")))
        Assertions.assertNotNull(post1)
        val post2 = postTestService.createPost(CreatePostDto(title = "title2", body = "body2", tags = listOf("All")))

        Assertions.assertNotNull(post2)
        Assertions.assertEquals(post1.writerId, post2.writerId)
        Assertions.assertEquals(post1.writerName, post2.writerName)

        val changedTitle = "changed...title.."
        val post1Id = postTestService.updatePost(PostUpdateDto(post1.id, changedTitle))
        val updatedPost1 = postTestService.findPost(post1Id.toString())

        Assertions.assertEquals(updatedPost1.title, changedTitle)
        Assertions.assertEquals(updatedPost1.body, post1.body)
        Assertions.assertIterableEquals(updatedPost1.tags, post1.tags)

        val updatedTags2 = listOf("All", "test1", "test3")
        val post2Id = postTestService.updatePost(PostUpdateDto(post2.id, tags = updatedTags2))
        val updatedPost2 = postTestService.findPost(post2Id.toString())
        Assertions.assertIterableEquals(updatedPost2.tags, updatedTags2.toSet())


        var deletedId = postTestService.deletePost(post2Id.toString())

        Assertions.assertThrows(NotFoundPost::class.java) {
            postTestService.findPost(deletedId.toString())
        }
    }

    @Test
    @Transactional
    fun tagCurd() {
        val deletedPromised = "리액트"
        val preparedTags = listOf("All", "test1", "test2", "test3") // 미리 data.sql에 준비된 태그들
        val tagNames = listOf(deletedPromised, "코틀린", "데이터베이스")
        val allTags = preparedTags + tagNames
        for (tagName in tagNames) {
            postTestService.upsertTag(tagName)
        }
        val foundTags = postTestService.getAllTags()
        Assertions.assertEquals(foundTags.size, preparedTags.size + tagNames.size)
        Assertions.assertEquals(foundTags.sorted(), allTags.sorted())

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