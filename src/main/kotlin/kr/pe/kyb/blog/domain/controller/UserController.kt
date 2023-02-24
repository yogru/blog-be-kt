package kr.pe.kyb.blog.domain.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import kr.pe.kyb.blog.domain.user.services.CreateUserDto
import kr.pe.kyb.blog.domain.user.services.JoinService
import kr.pe.kyb.blog.infra.anotation.RestV2
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

data class JoinUserRequest(
    @field:NotBlank(message = "이메일 입력 해주세요")
    val email: String,

    @field:NotBlank(message = "비밀번호 입력 해주세요")
    val password: String,

    @field:NotBlank
    val nickName: String
)


@RestV2
class UserController(
    val joinService: JoinService
) {
    @PostMapping("/user/join")
    fun joinUser(@RequestBody @Valid req: JoinUserRequest): UUID {
        return this.joinService.join(
            CreateUserDto(
                email = req.email,
                password = req.password,
                nickName = req.nickName
            )
        )
    }


}