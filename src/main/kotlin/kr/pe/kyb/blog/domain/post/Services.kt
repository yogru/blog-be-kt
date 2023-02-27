package kr.pe.kyb.blog.domain.post

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.pe.kyb.blog.infra.spring.MySpringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class CreatePostDto(
    val userId: UUID,
    val title: String,
    val body: String,
    val tags: List<String>
)


@Service
@Transactional(readOnly = true)
class PostService(
    val postRepository: PostRepository
) {

    @PersistenceContext
    lateinit var em: EntityManager
//    private fun getOrCreateUserValue(): PostUserValue {
//        val currentUserId = MySpringUtils.currentUserName
//        val currentUserValue = postRepository.findUserValueById(currentUserId)
//        if (currentUserValue != null) return currentUserValue
//
//        // PostUserValue(id=currentUserId, account = )
//    }

//    @Transactional()
//    fun createPost(dto: CreatePostDto): UUID = Post(
//        userId = dto.userId,
//        title = dto.title,
//        body = dto.body,
//        tags = dto.tags
//    ).also { postRepository.save(it) }
//        .let { it.id!! }

    @Transactional
    fun upsertTag(tagName: String) = postRepository.upsertTag(tagName)

    fun getAllTags(): Set<String> = postRepository.findAllTag().map { it.id }.toSet()

    @OptIn(ExperimentalStdlibApi::class)
    fun deleteTag(tagName: String) =
        postRepository.findTagById(tagName).let { it ?: throw NotFoundTag(tagName) }.also {
            if (tagName == "All") throw UnremovableTagException("All") else postRepository.deleteTagById(tagName)
        }
}