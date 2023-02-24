package kr.pe.kyb.blog.domain.user.services

import kr.pe.kyb.blog.domain.user.CreateFail
import kr.pe.kyb.blog.domain.user.CreateFailExistEmail

import kr.pe.kyb.blog.domain.user.models.UserEntity
import kr.pe.kyb.blog.domain.user.models.UserStatus
import kr.pe.kyb.blog.domain.user.repositories.UserQdRepository
import kr.pe.kyb.blog.domain.user.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class CreateUserDto(
    val email: String,
    val password: String,
    val nickName: String
)


interface JoinServiceInterface {
    fun join(user: CreateUserDto): UUID
}


@Service
@Transactional(readOnly = true)
class JoinService(
    val userQdRepository: UserQdRepository,
    val userRepository: UserRepository
) : JoinServiceInterface {

    @Transactional()
    override fun join(user: CreateUserDto): UUID {
        var foundUser = this.userRepository.findOneByAccount(user.email)
        println(foundUser)
        foundUser != null && throw CreateFailExistEmail(user.email)
        val createdUser = this.userRepository.save(
            UserEntity(
                account = user.email,
                password = user.password,
                status = UserStatus.SIGN,
                nickName = user.nickName
            )
        )
        return createdUser.id ?: throw CreateFail()
    }


}