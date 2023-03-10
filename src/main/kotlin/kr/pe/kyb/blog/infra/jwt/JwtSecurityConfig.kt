package kr.pe.kyb.blog.infra.jwt

import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import kr.pe.kyb.blog.infra.logger.Log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean


class JwtFilter(
        private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {
    companion object : Log {
        const val AUTHORIZATION_HEADER = "Authorization"
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse?, filterChain: FilterChain) {
        val token = resolveToken((request as HttpServletRequest?)!!)
        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    // 헤더에서 토큰 정보를 꺼내온다.
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

}


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(
        private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(
                        JwtFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter::class.java
                )
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}