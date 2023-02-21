package kr.pe.kyb.blog.user.unit

import jakarta.transaction.Transactional
import kr.pe.kyb.blog.domain.user.models.UserEntity
import kr.pe.kyb.blog.domain.user.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Transactional()
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository


    @Test
    @Transactional
    fun createUser() {
        val account = "test@gmail.com"
        val password = "1q2w3e4r1!"
        val status = "wait"
        val nickName = "kyb"

        val createdUser = this.userRepository.add(
            UserEntity(
                id = null,
                account = account,
                password = password,
                status = status,
                nickName = nickName
            )
        )

        println(createdUser.id)
        Assertions.assertNotNull(createdUser.id)
        Assertions.assertEquals(createdUser.account, account)
        Assertions.assertEquals(createdUser.password, password)
        Assertions.assertEquals(createdUser.status, status)
        Assertions.assertEquals(createdUser.nickName, nickName)
    }

}