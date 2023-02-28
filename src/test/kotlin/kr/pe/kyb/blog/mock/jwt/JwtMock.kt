package kr.pe.kyb.blog.mock.jwt

import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class JwtMock(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder
) {

    @Transactional
    fun genToken(id: UUID, password: String): JwtToken = jwtTokenProvider.generateToken(
        authenticationManagerBuilder.getObject()
            .authenticate(UsernamePasswordAuthenticationToken(id.toString(), password))
    )

}