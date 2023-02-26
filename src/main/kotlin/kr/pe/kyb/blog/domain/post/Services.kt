package kr.pe.kyb.blog.domain.post

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

data class CreatePostDto(
    val userId: UUID,
    val title: String,
    val body: String,
    val tags: List<String>
)


@Service
@Transactional(readOnly = true)
class PostService(
    val postRepository: PostRepository,
    val tagRepository: TagRepository
) {

    @Transactional()
    fun createPost(dto: CreatePostDto): UUID = Post(
        userId = dto.userId,
        title = dto.title,
        body = dto.body,
        tags = dto.tags
    ).also { postRepository.save(it) }
        .let { it.id!! }

    @Transactional
    fun upsertTag(tagName: String) = tagRepository.upsert(tagName)

    fun getAllTags(): Set<String> = tagRepository.findAll().map { it.id }.toSet()

    @OptIn(ExperimentalStdlibApi::class)
    fun deleteTag(tagName: String) =
        tagRepository.findById(tagName).getOrNull().let { it ?: throw NotFoundTag(tagName) }.also {
            if (tagName == "All") throw UnremovableTagException("All") else tagRepository.deleteById(tagName)
        }
}