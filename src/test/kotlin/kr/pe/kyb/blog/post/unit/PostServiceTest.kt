package kr.pe.kyb.blog.post.unit

import jakarta.annotation.Resource
import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.mock.TestConfig
import kr.pe.kyb.blog.mock.user.data.TestUserDto
import kr.pe.kyb.blog.mock.user.data.createMockTestUser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@SpringJUnitConfig(TestConfig::class)
class PostServiceTest {

    @Autowired
    lateinit var joinService: JoinService

    @Autowired
    lateinit var testUser: TestUserDto

    @Autowired
    lateinit var postTestService: PostService



    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(joinService, testUser)
    }


    @Test
    @Transactional
    fun createPost() {
        var ret1 = postTestService.createPost(CreatePostDto(title = "title", body = "body", tags = listOf("All")))
        Assertions.assertNotNull(ret1)
        var ret2 = postTestService.createPost(CreatePostDto(title = "title2", body = "body2", tags = listOf("All")))
        Assertions.assertNotNull(ret2)
        Assertions.assertEquals(ret1.writerId, ret2.writerId)
        Assertions.assertEquals(ret1.writerName, ret2.writerName)
    }

    @Test
    @Transactional
    fun tagCurd() {
        var deletedPromised = "리액트"
        var tagNames = listOf("All", deletedPromised, "코틀린", "데이터베이스")
        for (tagName in tagNames) {
            postTestService.upsertTag(tagName)
        }
        val foundTags = postTestService.getAllTags()
        Assertions.assertEquals(foundTags.size, 4)
        Assertions.assertEquals(foundTags.sorted(), tagNames.sorted())

        postTestService.deleteTag(deletedPromised)
        Assertions.assertFalse(postTestService.getAllTags().contains(deletedPromised))

        Assertions.assertThrows(UnremovableTagException::class.java) {
            postTestService.deleteTag("All")
        }
        Assertions.assertThrows(NotFoundTag::class.java) {
            postTestService.deleteTag(deletedPromised)
        }
    }

}