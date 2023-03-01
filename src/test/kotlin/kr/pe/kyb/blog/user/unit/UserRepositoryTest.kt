package kr.pe.kyb.blog.user.unit

import kr.pe.kyb.blog.domain.user.*
import kr.pe.kyb.blog.mock.user.data.createMockTestUser
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

    @Autowired
    lateinit var userManageService: UserManageService

    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(joinService, UUID.fromString(uuidString))
    }

    @Test
    @Transactional
    @WithUserDetails(value = uuidString, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    fun checkAuthUser() {
        var currentUser = userManageService.getCurrentUser()
        Assertions.assertEquals(currentUser.id.toString(), uuidString)

    }

}