package kr.pe.kyb.blog.domain.post

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.domain.post.infra.PostUserRepositoryInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class CreatePostDto(
    val title: String,
    val body: String,
    val tags: List<String>
)

data class CreatedPostDto(
    val id: String,
    val title: String,
    val body: String,
    val tags: Set<String>,
    val writerId: UUID,
    val writerName: String,
    val writerEmail: String
)

@Service
@Transactional(readOnly = true)
class PostService(
    val postRepository: PostRepository,
    val postUserRepository: PostUserRepositoryInterface
) {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Transactional
    fun getOrCreateUserValue(): PostUserValue {
        val userDto = postUserRepository.findCurrentUser()
        val currentUserValue = postRepository.findOneUserValueById(userDto.id)
        if (currentUserValue != null) return currentUserValue
        var ret = PostUserValue(id = userDto.id, account = userDto.email, nickName = userDto.nickName)
        entityManager.persist(ret)
        return ret
    }

    @Transactional
    fun createPost(dto: CreatePostDto): CreatedPostDto {
        return Post(
            title = dto.title,
            body = dto.body,
            tags = dto.tags,
            writer = getOrCreateUserValue()
        )
            .let { postRepository.save(it) }
            .let {
                CreatedPostDto(
                    id = it.id!!.toString(),
                    title = it.title,
                    body = it.body,
                    tags = it.tags,
                    writerId = it.writerId,
                    writerEmail = it.writerEmail,
                    writerName = it.writerName
                )
            }
    }


    @Transactional
    fun upsertTag(tagName: String) = postRepository.upsertTag(tagName)

    fun getAllTags(): Set<String> = postRepository.findAllTag().map { it.id }.toSet()

    @OptIn(ExperimentalStdlibApi::class)
    fun deleteTag(tagName: String) =
        postRepository.findTagById(tagName).let { it ?: throw NotFoundTag(tagName) }.also {
            if (tagName == "All") throw UnremovableTagException("All") else postRepository.deleteTagById(tagName)
        }
}