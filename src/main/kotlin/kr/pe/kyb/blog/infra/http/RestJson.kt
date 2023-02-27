package kr.pe.kyb.blog.infra.http

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.infra.spring.MySpringUtils
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestJson(
    val jwtTokenProvider: JwtTokenProvider
) {
    private fun makeHeader(isSetAccessKey: Boolean = true): HttpHeaders {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        if (isSetAccessKey) {
            var ret = jwtTokenProvider.generateToken(MySpringUtils.authentication)
            headers.setBearerAuth(ret.accessToken)
        }
        return headers
    }


    fun <T : Any> get(returnType: Class<T>, url: String, isSetAccessKey: Boolean = true): T {
        val restTemplate = RestTemplate()
        val headers = makeHeader(isSetAccessKey)
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            String::class.java
        )
        val mapper = jacksonObjectMapper()
        return mapper.readValue(response.body, returnType)
    }
}