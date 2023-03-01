package kr.pe.kyb.blog.domain.post.infra

import kr.pe.kyb.blog.domain.user.CurrentUserResponse
import kr.pe.kyb.blog.infra.config.ConstantValue
import kr.pe.kyb.blog.infra.http.RestJson
import org.springframework.stereotype.Repository
import java.util.*

data class CurrentUserDto(
    val id: UUID,
    val email: String,
    val nickName: String,
)

interface PostUserRepositoryInterface {
    fun findCurrentUser(): CurrentUserDto
}


@Repository
class PostUserRepository(
    private val restJson: RestJson
): PostUserRepositoryInterface {
    override fun findCurrentUser(): CurrentUserDto {
        val url = ConstantValue.INTERNAL_HTTP + "/user"
        val res = restJson.get(CurrentUserResponse::class.java, url)
        return CurrentUserDto(id = res.user.id, email = res.user.email, nickName = res.user.nickName)
    }

}