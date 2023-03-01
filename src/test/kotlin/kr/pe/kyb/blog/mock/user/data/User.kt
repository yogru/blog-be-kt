package kr.pe.kyb.blog.mock.user.data

import kr.pe.kyb.blog.domain.user.CreateUserDto
import kr.pe.kyb.blog.domain.user.JoinService
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


const val defaultTestUserUuidString = "aac48586-aad4-4196-ae9b-d89f53aa6bb1"

data class TestUserDto(
    var uuidString: String = defaultTestUserUuidString,
    var userPassword: String = "1q2w3e4r1!",
    var email: String = "test@kyb.pe.kr",
    var nickName: String = "tester",
)


fun createMockTestUser(
    joinService: JoinService,
    dto: TestUserDto,
): UUID {
    val ret = joinService.userRepository.findOneById(UUID.fromString(dto.uuidString))
    if (ret != null) return ret.id!!
    return joinService.join(
        CreateUserDto(
            id = dto.uuidString,
            email = dto.email,
            password = dto.userPassword,
            nickName = dto.nickName
        )
    )
}

