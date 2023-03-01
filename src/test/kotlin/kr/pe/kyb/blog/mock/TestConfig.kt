package kr.pe.kyb.blog.mock

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pe.kyb.blog.domain.post.PostRepository
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.domain.post.infra.CurrentUserDto
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.post.PostUserRepositoryMock
import kr.pe.kyb.blog.mock.user.data.TestUserDto
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.MockMvc
import java.util.*




@TestConfiguration
class TestConfig(
    var objectMapper: ObjectMapper,
    var jwtTokenProvider: JwtTokenProvider,
    var mockMvc: MockMvc,
    var postRepository: PostRepository,
) {
    @Bean
    fun mockMvcWrapper(): MockMvcWrapper {
        return MockMvcWrapper(objectMapper, jwtTokenProvider, mockMvc)
    }

    @Bean
    fun testUser(): TestUserDto {
        return TestUserDto()
    }

    @Primary
    @Bean
    fun postService(): PostService {
        var t = TestUserDto()
        return PostService(
            postRepository,
            PostUserRepositoryMock(
                CurrentUserDto(
                    id = UUID.fromString(t.uuidString),
                    email = t.email,
                    nickName = t.nickName
                )
            )
        )
    }

}