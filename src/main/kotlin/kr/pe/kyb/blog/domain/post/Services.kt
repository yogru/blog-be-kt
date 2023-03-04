package kr.pe.kyb.blog.domain.post

import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import kr.pe.kyb.blog.domain.post.infra.PostUserRepositoryInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

data class CreatePostDto(
    @field:NotEmpty
    val title: String,

    @field:NotEmpty
    val body: String,

    @field:NotEmpty
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

data class PostUserValueDto constructor(
    val writerName: String,
    val writerEmail: String
) {
    companion object {
        fun mapping(u: PostUserValue): PostUserValueDto {
            return PostUserValueDto(
                writerName = u.nickName,
                writerEmail = u.account,
            )
        }
    }
}

data class PostDto constructor(
    val id: String,
    val title: String,
    val body: String,
    val tags: Set<String>,
    val writer: PostUserValueDto
) {
    companion object {
        fun mapping(p: Post): PostDto {
            return PostDto(
                id = p.id!!.toString(),
                title = p.title,
                body = p.body,
                tags = p.tags,
                writer = PostUserValueDto.mapping(p.writer)
            )
        }
    }
}

data class PostUpdateDto(
    @field:NotEmpty
    val id: String,

    val title: String? = null,
    val body: String? = null,
    val tags: List<String>? = null
)


data class TagDto(
    val tagName: String
)

data class CreateSeriesDto(
    @field:NotBlank
    val title: String,
    val body: String?,
    val postIdList: List<UUID> = ArrayList<UUID>()
)

@Service
@Transactional(readOnly = true)
class PostService(
    val postUserRepository: PostUserRepositoryInterface,
    val repo: PostAggregateRepository
) {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Transactional
    fun getOrCreateUserValue(): PostUserValue {
        val userDto = postUserRepository.findCurrentUser()
        val currentUserValue = repo.findUserValueById(userDto.id)
        if (currentUserValue != null) return currentUserValue
        val ret = PostUserValue(id = userDto.id, account = userDto.email, nickName = userDto.nickName)
        entityManager.persist(ret)
        return ret
    }

    @Transactional
    fun createPost(@Valid dto: CreatePostDto): CreatedPostDto {
        return Post(
            title = dto.title,
            body = dto.body,
            tags = dto.tags,
            writer = getOrCreateUserValue()
        )
            .let { repo.persist(it) }
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
    fun deletePost(id: String): UUID {
        return repo.findPostById(UUID.fromString(id))
            .let { it ?: throw NotFoundPost(id) }
            .let {
                repo.remove(it)
                UUID.fromString(id)
            }
    }

    @Transactional
    fun updatePost(@Valid dto: PostUpdateDto): UUID {
        return repo.findPostById(UUID.fromString(dto.id))
            .let { it ?: throw NotFoundPost(dto.id) }
            .let {
                it.update(title = dto.title, body = dto.body, tags = dto.tags)
                it.id!!
            }
    }

    fun fetchPost(id: String): PostDto {
        return repo.findByIdFetchUserValue(UUID.fromString(id))
            .let { it ?: throw NotFoundPost(id) }
            .let {
                PostDto.mapping(it)
            }
    }

    @Transactional
    fun upsertTag(tagName: String) {
        repo.upsertTag(tagName)
    }

    fun getAllTags(): Set<String> {
        return repo.findAllTag().map { it.id }.toSet()
    }


    fun findTag(tagName: String): TagDto {
        return repo.findTagById(tagName)
            .let { it ?: throw NotFoundTag(tagName) }
            .let {
                TagDto(tagName = it.id)
            }
    }

    fun deleteTag(tagName: String) {
        if (tagName == "All") throw UnremovableTagException(tagName)
        return repo.findTagById(tagName)
            .let { it ?: throw NotFoundTag(tagName) }
            .let { repo.remove(it) }
            .let { tagName }
    }

    @Transactional
    fun createSeries(dto: CreateSeriesDto) {
        var user = getOrCreateUserValue()
        Series(
            userId = user.id,
            title = dto.title,
            body = dto.body,
            postIdList = dto.postIdList
        ).let { repo.persist(it) }
    }

}