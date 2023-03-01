package kr.pe.kyb.blog.mock.user.data

import kr.pe.kyb.blog.domain.user.CreateUserDto
import kr.pe.kyb.blog.domain.user.JoinService
import java.util.UUID

fun createMockTestUser(
    joinService: JoinService,
    id: UUID,
    email: String = "test@kybtest.com",
    password: String = "test1q2w3e4r1!",
    nickName: String = "testUser"
): UUID = joinService.join(
    CreateUserDto(
        id = id.toString(),
        email = email,
        password = password,
        nickName = nickName
    )
)
