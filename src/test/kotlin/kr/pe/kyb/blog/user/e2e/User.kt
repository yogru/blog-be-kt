package kr.pe.kyb.blog.user.e2e


import kr.pe.kyb.blog.domain.user.*
import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.TestUserDto

import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.testUserIdString
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.*


@MyTest
class User {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var testUserDto: TestUserDto

    @Test
    @WithMockUser(username = testUserIdString, roles = ["USER"], setupBefore = TestExecutionEvent.TEST_METHOD)
    fun currentUserDtoCheck() {
        val res = mockMvcWrapper.get(
            CurrentUserResponse::class.java,
            "/api/v2/user",
            WithUser(id = UUID.fromString(testUserIdString))
        )
        Assertions.assertEquals(res.user.id.toString(), testUserIdString)
    }

    @Test
    fun loginTest() {
        val res = mockMvcWrapper.post(
            JwtToken::class.java,
            "/api/v2/user/login",
            LoginUserRequest(email = testUserDto.account, password = testUserDto.password)
        )
        Assertions.assertNotNull(res.accessToken)
        Assertions.assertTrue(jwtTokenProvider.validateToken(res.accessToken))
    }

    @Test
    @Transactional
    fun joinUser() {
        val newEmail = "kybdev@kyb.pe.kr"
        val newPassword = "1Adkljvio23uo23nsd)!$#@mnz!"
        val nickName = "kybdev"
        val res = mockMvcWrapper.post(
            JoinUserResponse::class.java,
            "/api/v2/user/join",
            JoinUserRequest(email = newEmail, password = newPassword, nickName = nickName)
        )
        Assertions.assertNotNull(res)
        Assertions.assertNotNull(res.userId)
    }

}