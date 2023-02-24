package kr.pe.kyb.blog.domain.user.services

import kr.pe.kyb.blog.domain.user.CreateFailExistEmail
import kr.pe.kyb.blog.domain.user.models.UserEntity
import kr.pe.kyb.blog.domain.user.models.UserRole
import kr.pe.kyb.blog.domain.user.models.UserStatus
import kr.pe.kyb.blog.domain.user.repositories.UserRepository
import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.infra.logger.Log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.HashSet

data class CreateUserDto(
    val email: String,
    val password: String,
    val nickName: String
)


interface JoinServiceInterface {
    fun join(user: CreateUserDto): UUID
}


@Service
class CustomUserDetailsService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    companion object : Log {}

    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = this.userRepository.findOneByAccount(username)
        userEntity ?: throw UsernameNotFoundException("해당 유저 $username 찾을 수 없습니다.")
        logger().info("아 시바")
        var r = userEntity.roles.toTypedArray()
        logger().info(r.toString())
        return User.builder()
            .username(username)
            .password(passwordEncoder.encode(userEntity.password))
            .roles(*userEntity.roles.toTypedArray())
            .build()
    }
}

@Service
@Transactional(readOnly = true)
class JoinService(
    val jwtTokenProvider: JwtTokenProvider,
    val userRepository: UserRepository,
    val authenticationManagerBuilder: AuthenticationManagerBuilder
) : JoinServiceInterface {
    companion object : Log {}

    @Transactional()
    override fun join(user: CreateUserDto): UUID {
        this.userRepository.findOneByAccount(user.email)?.let {
            throw CreateFailExistEmail(it.account)
        }
        return UserEntity(
            account = user.email,
            password = user.password,
            status = UserStatus.SIGN,
            nickName = user.nickName,
        ).also { userRepository.save(it) }
            .let { it.id!! }
    }

    @Transactional()
    fun login(account: String, password: String): JwtToken {
        val authenticationToken = UsernamePasswordAuthenticationToken(account, password)
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        val authentication: Authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        logger().info(authentication.authorities.toString())
        return jwtTokenProvider.generateToken(authentication)
    }


}