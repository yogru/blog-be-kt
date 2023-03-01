package kr.pe.kyb.blog.user.unit

import kr.pe.kyb.blog.domain.user.*
import kr.pe.kyb.blog.mock.TestConfig
import kr.pe.kyb.blog.mock.user.data.TestUserDto

import kr.pe.kyb.blog.mock.user.data.createMockTestUser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.Transactional
import java.util.*


private const val uuidString = "08c48586-aad4-4196-ae9b-d89f53aa6bb0"

@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig::class)
class UserServiceTest {


    @Autowired
    lateinit var joinService: JoinService

    @Autowired
    lateinit var userManageService: UserManageService

    @Autowired
    lateinit var testUser: TestUserDto

    @BeforeEach
    @Transactional
    fun createTestUser() {
        val newTest = testUser.copy(uuidString = uuidString)
        println(newTest)
        createMockTestUser(joinService, newTest)
    }

    @Test
    @Transactional
    @WithMockUser(username = uuidString, roles = ["USER"])
    fun checkAuthUser() {
        var currentUser = userManageService.getCurrentUser()
        Assertions.assertEquals(currentUser.id.toString(), uuidString)
    }

}