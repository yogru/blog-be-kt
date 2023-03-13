package kr.pe.kyb.blog.infra.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import kr.pe.kyb.blog.infra.logger.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


data class JwtToken(
        val grantType: String,
        val accessToken: String,
        val refreshToken: String
)


@Component
class JwtTokenProvider {
    companion object : Log {}

    @Value("\${jwt.secret-key}")
    lateinit var jwtSecretKey: String

    val key: Key
        get() {
            val keyBytes: ByteArray = Decoders.BASE64.decode(this.jwtSecretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    fun generateToken(username: String, roles: List<String>): JwtToken {
        val authorities = roles.joinToString(",")
        val now: Long = Date().time

        val accessTokenExpiresIn = Date(now + 86400000)
        val accessToken: String = Jwts.builder()
                .setSubject(username)
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact()

        // Refresh Token 생성
        val refreshToken: String = Jwts.builder()
                .setExpiration(Date(now + 86400000))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact()

        return JwtToken(
                grantType = "Bearer",
                accessToken = accessToken,
                refreshToken = refreshToken
        )
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    fun generateToken(authentication: Authentication): JwtToken {
        return this.generateToken(
                username = authentication.name,
                roles = authentication.authorities.map { it.toString() }
        )
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    fun getAuthentication(accessToken: String): Authentication {
        val claims: Claims = parseClaims(accessToken)
        if (claims["auth"] == null) {
            throw RuntimeException("권한 정보가 없는 토큰입니다.")
        }
        // 클레임에서 권한 정보 가져오기
        val authorities: Collection<GrantedAuthority?> = claims["auth"].toString().split(",").map {
            SimpleGrantedAuthority(it)
        }
        // UserDetails 객체를 만들어서 Authentication 리턴
        val principal: UserDetails = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    // 토큰 정보를 검증하는 메서드
    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            logger().info("Invalid JWT Token", e)
        } catch (e: MalformedJwtException) {
            logger().info("Invalid JWT Token", e)
        } catch (e: ExpiredJwtException) {
            logger().info("Expired JWT Token", e)
        } catch (e: UnsupportedJwtException) {
            logger().info("Unsupported JWT Token", e)
        } catch (e: IllegalArgumentException) {
            logger().info("JWT claims string is empty.", e)
        }
        return false
    }

    private fun parseClaims(accessToken: String): Claims = try {
        Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(accessToken).body
    } catch (e: ExpiredJwtException) {
        e.claims
    }


}
