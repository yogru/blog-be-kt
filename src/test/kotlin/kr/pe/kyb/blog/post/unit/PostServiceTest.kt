package kr.pe.kyb.blog.post.unit

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.domain.post.infra.CurrentUserDto
import kr.pe.kyb.blog.domain.user.JoinService
import kr.pe.kyb.blog.infra.persistence.EntityManagerWrapper
import kr.pe.kyb.blog.mock.post.PostUserRepositoryMock
import kr.pe.kyb.blog.mock.user.data.createMockTestUser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


const val uuidString = "aac48586-aad4-4196-ae9b-d89f53aa6bb1"
const val userPassword = "1q2w3e4r1!"
const val email = "test@kyb.pe.kr"
const val nickName = "tester"


@Transactional(readOnly = true)
@SpringBootTest
@AutoConfigureMockMvc
class PostServiceTest {

    @Autowired
    lateinit var postRepository: PostRepository

    @Autowired
    lateinit var joinService: JoinService

    lateinit var postService: PostService
    
    @BeforeEach
    @Transactional
    fun createTestUser() {
        createMockTestUser(
            joinService,
            id = UUID.fromString(uuidString),
            email = email,
            password = userPassword,
            nickName = nickName
        )
    }

    @BeforeEach
    fun makePostService() {
        postService = PostService(
            postRepository = postRepository,
            postUserRepository = PostUserRepositoryMock(
                CurrentUserDto(
                    id = UUID.fromString(uuidString),
                    email = email,
                    nickName = nickName
                )
            )
        )
    }

    @Test
    @Transactional
    fun createPost() {
        var ret1 = postService.createPost(CreatePostDto(title = "title", body = "body", tags = listOf("All")))
        Assertions.assertNotNull(ret1)
        var ret2 = postService.createPost(CreatePostDto(title = "title2", body = "body2", tags = listOf("All")))
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
            postService.upsertTag(tagName)
        }
        val foundTags = postService.getAllTags()
        Assertions.assertEquals(foundTags.size, 4)
        Assertions.assertEquals(foundTags.sorted(), tagNames.sorted())

        postService.deleteTag(deletedPromised)
        Assertions.assertFalse(postService.getAllTags().contains(deletedPromised))

        Assertions.assertThrows(UnremovableTagException::class.java) {
            postService.deleteTag("All")
        }
        Assertions.assertThrows(NotFoundTag::class.java) {
            postService.deleteTag(deletedPromised)
        }
    }


}