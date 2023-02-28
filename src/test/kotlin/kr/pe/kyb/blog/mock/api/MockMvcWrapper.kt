package kr.pe.kyb.blog.mock.api

import com.fasterxml.jackson.databind.ObjectMapper
import kr.pe.kyb.blog.mock.jwt.JwtMock
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext
import java.util.*


data class WithUser(
    val id: UUID,
    val password: String
)

@Component
class MockMvcWrapper(
    private val objectMapper: ObjectMapper,
    private val jwtMock: JwtMock,
    private val mockMvc: MockMvc,
//    wac: WebApplicationContext
) {
//    private val mockMvc: MockMvc =
//        MockMvcBuilders.webAppContextSetup(wac).apply<DefaultMockMvcBuilder?>(springSecurity()).build()


    private fun setUpAccessKey(
        build: MockHttpServletRequestBuilder,
        withUser: WithUser?
    ): MockHttpServletRequestBuilder {
        if (withUser != null) {
            val token = jwtMock.genToken(withUser.id, withUser.password)
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