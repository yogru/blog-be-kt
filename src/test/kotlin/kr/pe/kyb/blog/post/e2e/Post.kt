package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.PostCreateReq
import kr.pe.kyb.blog.domain.post.PostCreatedRes
import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.mock.TestConfig
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.user.data.TestUserDto
import kr.pe.kyb.blog.mock.user.data.createMockTestUser
import kr.pe.kyb.blog.mock.user.data.defaultTestUserUuidString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.Transactional
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig::class)
class Post {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Autowired
    lateinit var joinService: JoinService

    @Autowired
    lateinit var testUser: TestUserDto

    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(joinService, testUser)
    }


    @Test
    @WithMockUser(
        username = defaultTestUserUuidString, roles = ["USER"]
    )
    fun createPost() {
        val title = "test_post"
        val body = "test_body"
        val tags = listOf("All")
        val res = mockMvcWrapper.post(
            PostCreatedRes::class.java, "/api/v2/post",
            PostCreateReq(title = title, body = body, tags = tags),
            WithUser(UUID.fromString(defaultTestUserUuidString))
        )

        val res1 = mockMvcWrapper.post(
            PostCreatedRes::class.java, "/api/v2/post",
            PostCreateReq(title = title, body = body, tags = tags),
            WithUser(UUID.fromString(defaultTestUserUuidString))
        )

        Assertions.assertNotNull(res.id)
        Assertions.assertNotNull(res1.id)
        Assertions.assertNotEquals(res.id,res1.id)
    }


}