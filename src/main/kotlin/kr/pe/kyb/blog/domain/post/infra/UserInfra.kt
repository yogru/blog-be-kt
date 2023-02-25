package kr.pe.kyb.blog.domain.post.infra

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

@Transactional(readOnly = true)
@Service
class UserInfra(
) {

//    fun getCurrentUser(): UserInfraDto {
//        var principal = SecurityContextHolder.getContext().authentication.principal
//        if (principal is UserDetails) {
//
//        }
//    }
}