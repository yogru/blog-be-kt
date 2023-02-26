package kr.pe.kyb.blog.domain.post.infra

import kr.pe.kyb.blog.domain.user.NotFoundUserDetail
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserPostInfraInterface {
    fun currentUserId(): UUID
}


@Service
@Transactional(readOnly = true)
class UserPostInfra
    : UserPostInfraInterface {
    override fun currentUserId(): UUID = SecurityContextHolder.getContext().authentication.principal
        .let { if (it is UserDetails) UUID.fromString(it.username) else throw NotFoundUserDetail() }
}