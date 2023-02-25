package kr.pe.kyb.blog.domain.user

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

data class CreateUserDto(
    val email: String,
    val password: String,
    val nickName: String
)


@Service
class CustomUserDetailsService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    companion object : Log {}

    override fun loadUserByUsername(username: String): UserDetails =
        this.userRepository.findOneByAccount(username).let {
            it ?: throw UsernameNotFoundException("해당 유저 $username 찾을 수 없습니다.")
        }.let {
            User.builder()
                .username(username)
                .password(it.password)
                .roles(*it.roles.toTypedArray())
                .build()
        }

}

@Service
@Transactional(readOnly = true)
class JoinService(
    val jwtTokenProvider: JwtTokenProvider,
    val userRepository: UserRepository,
    val authenticationManagerBuilder: AuthenticationManagerBuilder,
    val passwordEncoder: PasswordEncoder
) {
    companion object : Log {}

    @Transactional()
    fun join(user: CreateUserDto): UUID =
        this.userRepository.findOneByAccount(user.email).also {
            it != null && throw CreateFailExistEmail(it.account)
        }.let {
            UserEntity(
                account = user.email,
                password = passwordEncoder.encode(user.password),
                status = UserStatus.SIGN,
                nickName = user.nickName,
            ).also { userRepository.save(it) }
        }.let {
            it.id!!
        }

    @Transactional()
    fun login(account: String, password: String): JwtToken =
        this.userRepository.findOneByAccount(account).let {
            it ?: throw UsernameNotFoundException("해당 유저 $account 찾을 수 없습니다.")
        }.let {
            !passwordEncoder.matches(password, it.password) && throw RuntimeException("일치하지 않는 패스워드")

            authenticationManagerBuilder.getObject()
                .authenticate(UsernamePasswordAuthenticationToken(account, password))
        }.let {
            jwtTokenProvider.generateToken(it)
        }

}