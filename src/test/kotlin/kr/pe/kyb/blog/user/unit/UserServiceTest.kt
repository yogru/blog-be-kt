package kr.pe.kyb.blog.user.unit

import kr.pe.kyb.blog.domain.user.*
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.testUserIdString

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional


@MyTest
class UserServiceTest {

    @Autowired
    lateinit var userManageService: UserManageService

    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun checkAuthUser() {
        val currentUser = userManageService.getCurrentUser()
        Assertions.assertEquals(currentUser.id.toString(), testUserIdString)
    }

}