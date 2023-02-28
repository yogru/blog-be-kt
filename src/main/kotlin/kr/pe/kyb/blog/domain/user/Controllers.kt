package kr.pe.kyb.blog.domain.user

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.jwt.JwtToken
import kr.pe.kyb.blog.infra.logger.Log
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

data class JoinUserRequest(
    @field:NotBlank(message = "이메일 입력 해주세요")
    @field:Email
    val email: String,

    @field:NotBlank(message = "비밀번호 입력 해주세요")
    val password: String,

    @field:NotBlank
    val nickName: String
)

data class LoginUserRequest(
    @field:NotBlank(message = "이메일 입력 해주세요")
    @field:Email
    val email: String,

    @field:NotBlank(message = "비밀번호 입력 해주세요")
    val password: String,
)


data class CurrentUserResponse(
    val user: UserDto
)

data class JoinUserResponse(
    val userId: String
)

@RestV2
class UserController(
    val joinService: JoinService,
    val userManageService: UserManageService
) {
    companion object : Log {}


    @GetMapping("/user")
    fun getUser() = CurrentUserResponse(user = userManageService.getCurrentUser())

    @PostMapping("/user/join")
    fun joinUser(@RequestBody @Valid req: JoinUserRequest) = joinService.join(
        CreateUserDto(
            email = req.email,
            password = req.password,
            nickName = req.nickName
        )
    ).let { JoinUserResponse(userId = it.toString()) }


    @PostMapping("/user/login")
    fun loginUser(@RequestBody @Valid req: LoginUserRequest): JwtToken {
        return this.joinService.login(req.email, req.password)
    }


}