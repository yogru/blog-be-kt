package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.DeleteTagRes
import kr.pe.kyb.blog.domain.post.UpsertTagRes
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
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
    fun crd() {
        var newTag = "e2eTagTest"

        // create
        var createRes = mockMvcWrapper
            .withPostHeader("/api/v2/post/tag", UpsertTagRes(tag = newTag))
            .withBearerToken()
            .request(UpsertTagRes::class.java)
        Assertions.assertEquals(createRes.tag, newTag)

        // delete
        var deleteRes = mockMvcWrapper
            .withDeleteHeader("/api/v2/post/tag/${newTag}")
            .withBearerToken()
            .request(DeleteTagRes::class.java)

        Assertions.assertEquals(deleteRes.tag, newTag)

        var foundFailRes = mockMvcWrapper.withGetHeader("/api/v2/post/tag/${newTag}")
            .withBearerToken()
            .requestSimpleFail()

        Assertions.assertEquals(foundFailRes.statusCode, 404)
    }
}