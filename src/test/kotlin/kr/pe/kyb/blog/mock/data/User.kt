package kr.pe.kyb.blog.mock.data

import kr.pe.kyb.blog.domain.user.CreateUserDto
import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.domain.user.UserEntity
import java.util.UUID

fun createTestUser(
    joinService: JoinService,
    email: String = "test",
    password: String = "test1q2w3e4r1!",
    nickName: String = "testUser"
): UUID = joinService.join(CreateUserDto(email, password, nickName))