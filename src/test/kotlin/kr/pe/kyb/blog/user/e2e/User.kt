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
@DisplayName("e2e: user")
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
        val res = mockMvcWrapper.withGetHeader("/user")
                .withBearerToken()
                .request(CurrentUserResponse::class.java)
        Assertions.assertEquals(res.user.id.toString(), testUserIdString)
    }

    @Test
    fun loginTest() {
        val res = mockMvcWrapper.withPostHeader(
                "/user/login",
                LoginUserRequest(username = testUserDto.account, password = testUserDto.password)
        ).request(JwtToken::class.java)
        Assertions.assertNotNull(res.accessToken)
        Assertions.assertTrue(jwtTokenProvider.validateToken(res.accessToken))
    }

    @Test
    @Transactional
    fun joinUser() {
        val newEmail = "kybdev@kyb.pe.kr"
        val newPassword = "1Adkljvio23uo23nsd)!$#@mnz!"
        val nickName = "kybdev"
        val res = mockMvcWrapper
                .withPostHeader(
                        "/user/join",
                        JoinUserRequest(email = newEmail, password = newPassword, nickName = nickName)
                )
                .request(JoinUserResponse::class.java)
        Assertions.assertNotNull(res)
        Assertions.assertNotNull(res.userId)

        // 가입 직후 sign 상태로 로그인 불가능
        val loginRes = mockMvcWrapper.withPostHeader(
                "/user/login",
                LoginUserRequest(username = newEmail, password = newPassword)
        ).requestSimpleFail()
        Assertions.assertEquals(loginRes.statusCode, 401)
    }

}