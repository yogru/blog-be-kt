package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@MyTest
@DisplayName("unit: Post")
class Post {
    @Autowired
    lateinit var postTestService: PostService


    @Test
    @Transactional
    fun curd() {
        val post1 = postTestService.createPost(CreatePostDto(title = "title", body = "body", tags = listOf("All")))
        Assertions.assertNotNull(post1)
        val post2 = postTestService.createPost(CreatePostDto(title = "title2", body = "body2", tags = listOf("All")))

        Assertions.assertNotNull(post2)
        Assertions.assertEquals(post1.writerId, post2.writerId)
        Assertions.assertEquals(post1.writerName, post2.writerName)

        val changedTitle = "changed...title.."
        val post1Id = postTestService.updatePost(PostUpdateDto(post1.id, changedTitle))
        val updatedPost1 = postTestService.fetchPost(post1Id.toString())

        Assertions.assertEquals(updatedPost1.title, changedTitle)
        Assertions.assertEquals(updatedPost1.body, post1.body)
        Assertions.assertIterableEquals(updatedPost1.tags, post1.tags)

        val updatedTags2 = listOf("All", "test1", "test3")
        val post2Id = postTestService.updatePost(PostUpdateDto(post2.id, tags = updatedTags2))
        val updatedPost2 = postTestService.fetchPost(post2Id.toString())
        Assertions.assertIterableEquals(updatedPost2.tags.toList().sorted(), updatedTags2.sorted())


        var deletedId = postTestService.deletePost(post2Id.toString())

        Assertions.assertThrows(NotFoundPost::class.java) {
            postTestService.fetchPost(deletedId.toString())
        }
    }

    @Test
    @Rollback(false)
    @Transactional
    fun list() {
        val testTagNames = listOf(
            "react", "database", "it", "kotlin", "spring", "spring-boot",
            "os", "network", "study", "etc", "movie"
        )
        for (tagName in testTagNames) {
            postTestService.upsertTag(tagName)
        }

        for (i in 1..100) {
            postTestService.createPost(
                CreatePostDto(
                    title = "title_$i",
                    body = "body_$i",
                    tags = listOf(
                        "All",
                        testTagNames[i % testTagNames.size],
                        testTagNames[(i + 1) % testTagNames.size],
                        testTagNames[(i + 2) % testTagNames.size],
                    )
                )
            )
        }

        var posts = postTestService.listDynamicPost(
            PostCondition(
                tagNames = listOf(testTagNames[0])
            ), PageRequest.of(0, 10)
        )
        Assertions.assertEquals(posts.size, 10)
        val isContains = posts.map { it.tags.contains(testTagNames[0]) }
        Assertions.assertIterableEquals(isContains, (1..10).map { true })
    }

}