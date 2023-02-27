package kr.pe.kyb.blog.domain.post.infra

import kr.pe.kyb.blog.infra.config.ConstantValue
import kr.pe.kyb.blog.infra.http.RestJson
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*

data class UserPostDto(
    val id: UUID,
    val nickName: String,
    val email: String
)

@Component
class UserInfraRepository(
    val restJson: RestJson
) {

    fun getUser() {
        val url = ConstantValue.INTERNAL_HTTP + "/user"
        val ret = restJson.get(UserPostDto::class.java, url)
        println(ret.email)
    }


}