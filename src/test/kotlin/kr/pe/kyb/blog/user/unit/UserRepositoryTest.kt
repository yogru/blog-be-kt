package kr.pe.kyb.blog.user.unit

import kr.pe.kyb.blog.domain.user.UserEntity
import kr.pe.kyb.blog.domain.user.UserStatus
import kr.pe.kyb.blog.domain.user.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@SpringBootTest
class UserRepositoryTest {


    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun createUser() {
        val account = "test@gmail.com"
        val password = "1q2w3e4r1!"
        val nickName = "kyb"

        val createdUser = this.userRepository.save(
            UserEntity(
                account = account,
                password = password,
                status = UserStatus.SIGN,
                nickName = nickName
            )
        )

        println(createdUser.id)
        Assertions.assertNotNull(createdUser.id)
        Assertions.assertEquals(createdUser.account, account)
        Assertions.assertEquals(createdUser.password, password)
        Assertions.assertEquals(createdUser.status, UserStatus.SIGN)
        Assertions.assertEquals(createdUser.nickName, nickName)
    }

}