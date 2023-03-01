package kr.pe.kyb.blog.mock.api

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*


data class WithUser(
    val id: UUID,
    val roles: List<String> = listOf("USER")
)


class MockMvcWrapper(
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val mockMvc: MockMvc,
) {


    private fun setUpAccessKey(
        build: MockHttpServletRequestBuilder,
        withUser: WithUser?
    ): MockHttpServletRequestBuilder {
        if (withUser != null) {
            val token = jwtTokenProvider.generateToken(withUser.id.toString(), withUser.roles)
            return build.header("authentication", "Bearer " + token.accessToken)
        }
        return build
    }

    fun <T> get(clazz: Class<T>, url: String, withUser: WithUser? = null): T {
        var builders = setUpAccessKey(
            MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON),
            withUser
        )
        var mockRet = mockMvc.perform(builders)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        return objectMapper.readValue(mockRet.response.contentAsString, clazz)
    }

    fun <T, U> post(retClazz: Class<U>, url: String, body: T, withUser: WithUser? = null): U {
        var builders = setUpAccessKey(
            MockMvcRequestBuilders
                .post(url)
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON),
            withUser
        )

        var mockRet = mockMvc.perform(builders)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        return objectMapper.readValue(mockRet.response.contentAsString, retClazz)
    }

}