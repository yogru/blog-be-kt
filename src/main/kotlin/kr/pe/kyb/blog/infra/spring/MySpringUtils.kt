package kr.pe.kyb.blog.infra.spring

import kr.pe.kyb.blog.domain.user.NotFoundUserDetail
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*


class MySpringUtils {
    companion object {
        val authentication: Authentication
            get() = SecurityContextHolder.getContext().authentication

        val currentUserName: UUID
            get() = MySpringUtils.authentication.principal
                .let { if (it is UserDetails) UUID.fromString(it.username) else throw NotFoundUserDetail() }

    }

}