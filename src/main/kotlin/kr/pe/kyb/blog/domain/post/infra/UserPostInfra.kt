package kr.pe.kyb.blog.domain.post.infra

import kr.pe.kyb.blog.domain.user.UserInternalService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

data class UserInfraDto(
    val id: UUID,
    val account: String,
    val nickName: String
)

interface UserPostInfraInterface {
    fun getCurrentUser(): UserInfraDto
}


@Transactional(readOnly = true)
@Service
class UserPostInfra(
    val userInternalService: UserInternalService
) : UserPostInfraInterface {
    override fun getCurrentUser(): UserInfraDto = userInternalService.findByCurrentUserDetail()
        .let {
            UserInfraDto(
                id = it.id!!,
                account = it.account,
                nickName = it.nickName
            )
        }
}