package kr.pe.kyb.blog.mock.api

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pe.kyb.blog.infra.error.SimpleErrorResponse
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
        private val withUser: WithUser
) {
    private var headerBuilder: MockHttpServletRequestBuilder? = null
    private var baseUrl: String = "/api/v2"

    fun withBaseUrl(baseUrl: String): MockMvcWrapper {
        this.baseUrl = baseUrl
        return this
    }

    private fun preprocessUrl(url: String): String {
        return this.baseUrl + url
    }


    fun withBearerToken(): MockMvcWrapper {
        assert(headerBuilder != null)
        val token = jwtTokenProvider.generateToken(withUser.id.toString(), withUser.roles)
        headerBuilder!!.header("authentication", "Bearer " + token.accessToken)
        return this
    }

    fun withGetHeader(url: String): MockMvcWrapper {
        headerBuilder = MockMvcRequestBuilders
                .get(preprocessUrl(url))
                .contentType(MediaType.APPLICATION_JSON)
        return this
    }

    fun withDeleteHeader(url: String): MockMvcWrapper {
        headerBuilder = MockMvcRequestBuilders
                .delete(preprocessUrl(url))
                .contentType(MediaType.APPLICATION_JSON)
        return this
    }

    fun <T> withPostHeader(url: String, body: T): MockMvcWrapper {
        headerBuilder = MockMvcRequestBuilders
                .post(preprocessUrl(url))
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
        return this
    }

    fun <T> withPutHeader(url: String, body: T): MockMvcWrapper {
        headerBuilder = MockMvcRequestBuilders
                .put(preprocessUrl(url))
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
        return this
    }

    fun <T> request(clazz: Class<T>): T {
        assert(headerBuilder != null)
        var ret = mockMvc.perform(headerBuilder!!)
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        return objectMapper.readValue(ret.response.contentAsString, clazz)
    }

    fun requestSimpleFail(): SimpleErrorResponse {
        assert(headerBuilder != null)
        var mockRet = mockMvc.perform(headerBuilder!!).andReturn()
        return objectMapper.readValue(mockRet.response.contentAsString, SimpleErrorResponse::class.java)
    }

}