package kr.pe.kyb.blog.mock.post

import kr.pe.kyb.blog.domain.post.infra.CurrentUserDto
import kr.pe.kyb.blog.domain.post.infra.PostUserRepositoryInterface

class PostUserRepositoryMock(
    private val user: CurrentUserDto
) : PostUserRepositoryInterface {

    override fun findCurrentUser(): CurrentUserDto {
        return user
    }
}

