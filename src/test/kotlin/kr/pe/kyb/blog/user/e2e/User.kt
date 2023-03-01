package kr.pe.kyb.blog.user.e2e

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.domain.user.*
import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.user.data.createMockTestUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.*


const val uuidString = "aac48586-aad4-4196-ae9b-d89f53aa6bb1"
const val userPassword = "1q2w3e4r1!"
const val email = "test@kyb.pe.kr"

@SpringBootTest
@AutoConfigureMockMvc
class User {

    @Autowired
    lateinit var joinService: JoinService

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider




    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(joinService, id = UUID.fromString(uuidString), email = email, password = userPassword)
    }


    @Test
    @WithMockUser(username = uuidString, roles = ["USER"])
    fun currentUserDtoCheck() {
        var res = mockMvcWrapper.get(
            CurrentUserResponse::class.java,
            "/api/v2/user",
            WithUser(id = UUID.fromString(uuidString))
        )
        Assertions.assertEquals(res.user.id.toString(), uuidString)
    }

    @Test
    fun loginTest() {
        var res = mockMvcWrapper.post(
            JwtToken::class.java,
            "/api/v2/user/login",
            LoginUserRequest(email = email, password = userPassword)
        )
        Assertions.assertNotNull(res.accessToken)
        Assertions.assertTrue(jwtTokenProvider.validateToken(res.accessToken))
    }

    @Test
    fun joinUser() {
        val newEmail = "kybdev@kyb.pe.kr"
        val newPassword = "1Adkljvio23uo23nsd)!$#@mnz!"
        val nickName = "kybdev"

        var res = mockMvcWrapper.post(
            JoinUserResponse::class.java,
            "/api/v2/user/join",
            JoinUserRequest(email = newEmail, password = newPassword, nickName = nickName)
        )
        Assertions.assertNotNull(res)
        Assertions.assertNotNull(res.userId)
    }

}