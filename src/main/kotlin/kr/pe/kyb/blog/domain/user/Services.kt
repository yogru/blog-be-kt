package kr.pe.kyb.blog.domain.user

import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.jwt.JwtTokenProvider
import kr.pe.kyb.blog.infra.logger.Log
import kr.pe.kyb.blog.infra.persistence.EntityManagerWrapper
import kr.pe.kyb.blog.infra.spring.MySpringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

data class CreateUserDto(
    var id: String? = null, // 테스트 편의성 때문에 처음부터 uuid 정해진 객체를 만들어야할 필요가 있다.
    val email: String,
    val password: String,
    val nickName: String
)

data class UserDto(
    val id: UUID,
    val email: String,
    val nickName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val status: UserStatus
)

@Service
class CustomUserDetailsService(
    val userRepository: UserRepository
) : UserDetailsService {
    companion object : Log {}

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findOneById(UUID.fromString(username))
            .let { it ?: throw UsernameNotFoundException("해당 유저 $username 찾을 수 없습니다.") }
            .let {
                User.builder()
                    .username(username)
                    .password(it.password)
                    .roles(*it.roles.toTypedArray())
                    .build()
            }
}


@Service
class UserManageService(
    val userRepository: UserRepository
) {


    fun getCurrentUser(): UserDto =
        userRepository.findOneById(MySpringUtils.currentUserName)
            .let { it ?: throw NotFoundCurrentUser() }
            .let {
                UserDto(
                    id = it.id!!,
                    email = it.account,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    nickName = it.nickName,
                    status = it.status
                )
            }
}


@Service
@Transactional(readOnly = true)
class JoinService(
    val jwtTokenProvider: JwtTokenProvider,
    val userRepository: UserRepository,
    val authenticationManagerBuilder: AuthenticationManagerBuilder,
    val passwordEncoder: PasswordEncoder,
    val em: EntityManagerWrapper
) {
    companion object : Log {}

    @Transactional()
    fun join(user: CreateUserDto): UUID = userRepository.findOneByAccount(user.email)
        .let { it != null && throw CreateFailExistEmail(it.account) }
        .let {
            UserEntity(
                id = if (user.id != null) UUID.fromString(user.id) else null,
                account = user.email,
                password = passwordEncoder.encode(user.password),
                status = UserStatus.SIGN,
                nickName = user.nickName,
            )
        }
        .let { em.make(it) }
        .let { it.id!! }

    @Transactional()
    fun login(account: String, password: String): JwtToken = userRepository.findOneByAccount(account)
        .let { it ?: throw UsernameNotFoundException("해당 유저 $account 찾을 수 없습니다.") }
        .also { !passwordEncoder.matches(password, it.password) && throw RuntimeException("일치하지 않는 패스워드") }
        .let {
            logger().info(account)
            jwtTokenProvider.generateToken(
                authenticationManagerBuilder.getObject()
                    .authenticate(UsernamePasswordAuthenticationToken(it.id!!.toString(), password))
            )
        }

}