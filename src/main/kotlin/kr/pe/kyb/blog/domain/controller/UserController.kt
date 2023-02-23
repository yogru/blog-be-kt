package kr.pe.kyb.blog.domain.controller

import kr.pe.kyb.blog.domain.user.services.CreateUserDto
import kr.pe.kyb.blog.domain.user.services.JoinService
import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.error.ControllerException
import kr.pe.kyb.blog.infra.error.ServiceException
import kr.pe.kyb.blog.infra.error.catchDomainException
import kr.pe.kyb.blog.infra.error.catchServiceException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

data class JoinUserRequest(
    val email: String,
    val password: String,
    val nickName: String
)


@RestV2
class UserController(
    val joinService: JoinService
) {
    @PostMapping("/user/join")
    fun joinUser(@RequestBody req: JoinUserRequest): UUID = catchServiceException(isDebug = true) {
        this.joinService.join(
            CreateUserDto(
                email = req.email,
                password = req.password,
                nickName = req.nickName
            )
        )
    }


}