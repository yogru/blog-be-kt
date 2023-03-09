package kr.pe.kyb.blog.domain.post

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kr.pe.kyb.blog.domain.post.infra.PostUserRepositoryInterface
import org.springframework.data.domain.Pageable
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

data class PostDto(
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
                    tags = p.tagNames,
                    writer = PostUserValueDto.mapping(p.writer)
            )
        }
    }
}


data class OrderedPostDto(
        val id: String,
        val orderNumber: Int,
        val title: String,
        val body: String,
        val tags: Set<String>,
        val writer: PostUserValueDto
) {
    companion object {
        fun mapping(p: Post, orderNumber: Int): OrderedPostDto {
            return OrderedPostDto(
                    id = p.id!!.toString(),
                    orderNumber = orderNumber,
                    title = p.title,
                    body = p.body,
                    tags = p.tagNames,
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
        val body: String = "",
        val postIds: List<UUID> = listOf()
)

data class SeriesDetailDto(
        val id: UUID,
        var title: String,
        val writer: PostUserValueDto,
        val body: String?,
        val posts: List<OrderedPostDto>
) {
    companion object {
        private fun getSortedPosts(series: Series): List<OrderedPostDto> {
            if (series.seriesPosts.isEmpty()) return listOf()
            return series.seriesPosts
                    .sortedBy { it.orderNumber }
                    .map { it }
                    .map { OrderedPostDto.mapping(it.post, it.orderNumber) }
        }

        fun mapping(series: Series): SeriesDetailDto {
            return SeriesDetailDto(
                    id = series.id!!,
                    title = series.title,
                    body = series.body,
                    writer = PostUserValueDto.mapping(series.writer),
                    posts = getSortedPosts(series)
            )
        }
    }
}


data class UpdateSeriesDto(
        @field:NotNull
        val id: UUID,
        var title: String? = null,
        val body: String? = null,
        val postIds: List<UUID>? = null
)

data class SeriesDto(
        val id: UUID,
        var title: String,
        val writer: PostUserValueDto,
        val body: String?,
        val postIds: List<UUID>
) {
    companion object {
        fun mapping(series: Series): SeriesDto {
            return SeriesDto(
                    id = series.id!!,
                    title = series.title,
                    body = series.body,
                    writer = PostUserValueDto.mapping(series.writer),
                    postIds = series.seriesPosts.map { it.post.id!! }
            )
        }
    }

}

data class TagStatistics(
        val tag: String,
        val count: Long
)


@Service
@Transactional(readOnly = true)
class PostService(
        val postUserRepository: PostUserRepositoryInterface,
        val repo: PostAggregateRepository
) {

    @Transactional
    fun getOrCreateUserValue(): PostUserValue {
        val userDto = postUserRepository.findCurrentUser()
        val currentUserValue = repo.findUserValueById(userDto.id)
        if (currentUserValue != null) return currentUserValue
        val ret = PostUserValue(id = userDto.id, account = userDto.email, nickName = userDto.nickName)
        repo.persist(ret)
        return ret
    }

    @Transactional
    fun createPost(@Valid dto: CreatePostDto): CreatedPostDto {
        // 캐싱 필요..
        var tags = repo.findTagInIds(dto.tags)
        return Post(
                title = dto.title,
                body = dto.body,
                tags = tags,
                writer = getOrCreateUserValue()
        )
                .let { repo.persist(it) }
                .let {
                    CreatedPostDto(
                            id = it.id!!.toString(),
                            title = it.title,
                            body = it.body,
                            tags = it.tagNames,
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
        var tags = repo.findTagInIds(dto.tags)
        return repo.findPostById(UUID.fromString(dto.id))
                .let { it ?: throw NotFoundPost(dto.id) }
                .let {
                    it.update(title = dto.title, body = dto.body, tags = tags)
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

    fun deleteTag(tagName: String): String {
        if (tagName == "All") throw UnremovableTagException(tagName)
        repo.findTagById(tagName)
                .let { it ?: throw NotFoundTag(tagName) }
                .let { repo.remove(it) }
        return tagName
    }


    fun getSortedPosts(postIds: List<UUID>): List<Post> {
        var posts = repo.findPostInIds(postIds)
        var postsMap = mutableMapOf<UUID, Post>()
        var sortedPosts = mutableListOf<Post>()
        posts!!.map { postsMap.put(it.id!!, it) }
        postIds.map { sortedPosts.add(postsMap[it]!!) }
        return sortedPosts
    }

    @Transactional
    fun createSeries(dto: CreateSeriesDto): UUID {
        var user = getOrCreateUserValue()
        var posts = getSortedPosts(dto.postIds.toSet().toList())

        return Series(
                writer = user,
                title = dto.title,
                body = dto.body,
                posts = posts,
        )
                .let { repo.persist(it) }
                .let {
                    it.id!!
                }
    }

    fun fetchSeries(id: UUID): SeriesDetailDto {
        return repo.fetchSeries(id).let {
            it ?: throw NotFoundSeries(id.toString())
        }.let { SeriesDetailDto.mapping(it) }
    }

    @Transactional
    fun removeSeries(id: UUID): UUID {
        return repo.findSeriesById(id).let {
            it ?: throw NotFoundSeries(id.toString())
        }.let {
            repo.remove(it)
            id
        }
    }

    @Transactional
    fun updateSeries(@Valid dto: UpdateSeriesDto): UUID {
        var posts = if (dto.postIds != null) getSortedPosts(dto.postIds) else null

        return repo.fetchSeries(dto.id).let {
            it ?: throw NotFoundSeries(dto.id.toString())
        }.let {
            it.update(
                    title = dto.title,
                    body = dto.body,
                    posts = posts
            )
            dto.id
        }
    }

    fun listSeries(pageable: Pageable): List<SeriesDto> {
        return repo.listSeries(pageable).map {
            SeriesDto.mapping(it)
        }
    }

    fun listDynamicPost(condition: PostCondition, pageable: Pageable): List<PostDto> {
        return repo.listPost(condition, pageable).map {
            PostDto.mapping(it)
        }
    }

    fun deleteSeries(seriesId: UUID) {
        repo.findSeriesById(seriesId).let {
            it ?: throw NotFoundSeries(seriesId.toString())
        }.let {
            repo.remove(it)
        }
    }

    fun getSeriesListByPostId(id: UUID): List<SeriesDetailDto> {
        return repo.listSeriesByPostId(id)
                .map { SeriesDetailDto.mapping(it) }
    }

    fun getTagStatistics(): List<TagStatistics> {
        return repo.getTagStatistics()
    }

}