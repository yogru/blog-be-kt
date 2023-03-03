package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.PostCreateReq
import kr.pe.kyb.blog.domain.post.PostCreatedRes
import kr.pe.kyb.blog.domain.post.PostRes
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.testUserIdString

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.*


@MyTest
class Post {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun createPost() {
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
        Assertions.assertNotNull(res.id)
        Assertions.assertNotNull(res1.id)
        Assertions.assertNotEquals(res.id, res1.id)

        var foundUser1 = mockMvcWrapper.get(
            PostRes::class.java,
            "/api/v2/post/${res.id}",
            WithUser(UUID.fromString(testUserIdString))
        )

        Assertions.assertEquals(foundUser1.post.title, title)
        Assertions.assertEquals(foundUser1.post.body, body)
        Assertions.assertIterableEquals(tags, foundUser1.post.tags)

    }


//    @Test
//    @Transactional
//    @WithMockUser
//    fun updatePost() {
//        val title = "test_post"
//        val body = "test_body"
//        val tags = listOf("All")
//        val res = mockMvcWrapper.post(
//            PostCreatedRes::class.java, "/api/v2/post",
//            PostCreateReq(title = title, body = body, tags = tags),
//            WithUser(UUID.fromString(testUserIdString))
//        )
//
//
//    }


}