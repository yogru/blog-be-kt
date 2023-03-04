package kr.pe.kyb.blog.mock

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pe.kyb.blog.domain.post.PostRepository
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.domain.post.infra.CurrentUserDto
import kr.pe.kyb.blog.domain.user.UserStatus
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.post.PostUserRepositoryMock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.MockMvc
import java.util.*


data class TestUserDto(
    val id: UUID,
    val account: String,
    val password: String,
    val nickName: String,
    val roles: List<String>,
    val status: UserStatus
)

const val testUserIdString = "f3503729-afb2-4890-984e-edd1575fb1c0"

@TestConfiguration
class TestConfig(
    var objectMapper: ObjectMapper,
    var jwtTokenProvider: JwtTokenProvider,
    var mockMvc: MockMvc,
    var postRepository: PostRepository,
) {
    @Bean
    fun testUser(): TestUserDto {
        /*
        * 현재 test/resources/data.sql 에서 test_user 생성한다.
        * 반드시 데이터베이스에 저장되는 유저와 반환하는 TestUserDto 값이 같아야한다.
        * */
        return TestUserDto(
            id = UUID.fromString(testUserIdString),
            account = "test@kyb.pe.kr",
            password = "1q2w3e4r",
            nickName = "test_user",
            status = UserStatus.valueOf("NORMAL"),
            roles = listOf("USER")
        )
    }


    @Bean
    fun mockMvcWrapper(): MockMvcWrapper {
        return MockMvcWrapper(
            objectMapper,
            jwtTokenProvider,
            mockMvc,
            WithUser(
                id = UUID.fromString(testUserIdString),
                roles = listOf("USER")
            )
        )
    }

    @Primary
    @Bean
    fun postService(): PostService {
        val t = this.testUser()
        return PostService(
            postRepository,
            PostUserRepositoryMock(
                CurrentUserDto(
                    id = t.id,
                    email = t.account,
                    nickName = t.nickName
                )
            )
        )
    }

}