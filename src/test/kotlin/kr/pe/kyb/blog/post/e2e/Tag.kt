package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.UpsertTagRes
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

@MyTest
@DisplayName("e2e: Tag")
class Tag {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun curd() {
        var newTag = "e2eTagTest"
        var res = mockMvcWrapper
            .withPostHeader("/api/v2/post/tag", UpsertTagRes(tag = newTag))
            .withBearerToken()
            .request(UpsertTagRes::class.java)

        Assertions.assertEquals(res.tag, newTag)
    }
}