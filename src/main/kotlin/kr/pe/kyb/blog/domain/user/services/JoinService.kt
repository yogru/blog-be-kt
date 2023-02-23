package kr.pe.kyb.blog.domain.user.services

import kr.pe.kyb.blog.domain.user.models.UserEntity
import kr.pe.kyb.blog.domain.user.models.UserStatus
import kr.pe.kyb.blog.domain.user.repositories.UserRepository
import kr.pe.kyb.blog.infra.error.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.NullPointerException
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
    val userRepository: UserRepository
) : JoinServiceInterface {

    @Transactional()
    override fun join(user: CreateUserDto): UUID = catchDomainException<UUID> {
        val createdUser = this.userRepository.add(
            UserEntity(
                account = user.email,
                password = user.password,
                status = UserStatus.SIGN,
                nickName = user.nickName
            )
        )
        createdUser.id ?: throw ServiceException.createException(
            ErrorCode(
                HttpCode.InternalServerError,
                "유저 생성 실패"
            )
        )
    }


}