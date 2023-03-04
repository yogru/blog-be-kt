package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.infra.error.SimpleErrorResponse
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.testUserIdString

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.*


@MyTest
@DisplayName("e2e: Post")
class Post {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun postCurd() {
        val title = "test_post"
        val body = "test_body"
        val tags = listOf("All")
        val res = mockMvcWrapper.post(
            PostCreatedRes::class.java, "/api/v2/post",
            PostCreateReq(title = title, body = body, tags = tags),
            WithUser(UUID.fromString(testUserIdString))
        )
        val res1 = mockMvcWrapper.post(
            PostCreatedRes::class.java, "/api/v2/post",
            PostCreateReq(title = title, body = body, tags = tags),
            WithUser(UUID.fromString(testUserIdString))
        )
        // Create
        Assertions.assertNotNull(res.id)
        Assertions.assertNotNull(res1.id)
        Assertions.assertNotEquals(res.id, res1.id)

        // Read
        var foundUser1 = mockMvcWrapper.get(
            PostRes::class.java,
            "/api/v2/post/${res.id}",
            WithUser(UUID.fromString(testUserIdString))
        )

        Assertions.assertEquals(foundUser1.post.title, title)
        Assertions.assertEquals(foundUser1.post.body, body)
        Assertions.assertIterableEquals(foundUser1.post.tags.sorted(), tags.sorted())

        val changedTitle = "changed..title"
        val changedBody = "changed...body"
        val changedTags = listOf("All", "test1", "test2")
        // update
        mockMvcWrapper.put(
            UpdatePostRes::class.java, "/api/v2/post",
            UpdatePostReq(res.id, changedTitle, changedBody, changedTags),
            WithUser(UUID.fromString(testUserIdString))
        )

        foundUser1 = mockMvcWrapper.get(
            PostRes::class.java,
            "/api/v2/post/${res.id}",
            WithUser(UUID.fromString(testUserIdString))
        )

        Assertions.assertEquals(foundUser1.post.title, changedTitle)
        Assertions.assertEquals(foundUser1.post.body, changedBody)
        Assertions.assertIterableEquals(foundUser1.post.tags.sorted(), changedTags.sorted())

        // delete
        val deletedPostId = mockMvcWrapper.delete(
            PostDeleteRes::class.java,
            "/api/v2/post/${foundUser1.post.id}",
            WithUser(UUID.fromString(testUserIdString))
        )

        val getFail = mockMvcWrapper.getFail(
            "/api/v2/post/${deletedPostId.id}",
            WithUser(UUID.fromString(testUserIdString))
        )
        Assertions.assertEquals(getFail.statusCode, 404)
    }

}