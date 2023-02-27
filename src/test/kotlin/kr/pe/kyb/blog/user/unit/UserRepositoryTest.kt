package kr.pe.kyb.blog.user.unit

import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.domain.user.UserEntity
import kr.pe.kyb.blog.domain.user.UserStatus
import kr.pe.kyb.blog.domain.user.UserRepository
import kr.pe.kyb.blog.mock.data.createMockTestUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.transaction.annotation.Transactional
import java.util.*


private const val uuidString = "08c48586-aad4-4196-ae9b-d89f53aa6bb0"

@Transactional(readOnly = true)
@SpringBootTest
class UserRepositoryTest {


    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var joinService: JoinService

    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(joinService, UUID.fromString(uuidString))
    }

    @Test
    @Transactional
    @WithUserDetails(value = uuidString, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    fun checkAuthUser() {
        var ret = userRepository.findOneById(UUID.fromString(uuidString))
        Assertions.assertNotNull(ret)
        Assertions.assertNotNull(ret?.id)
    }

}