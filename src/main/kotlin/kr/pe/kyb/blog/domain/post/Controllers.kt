package kr.pe.kyb.blog.domain.post

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import kr.pe.kyb.blog.infra.anotation.RestV2
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.concurrent.locks.Condition

data class PostCreateReq(
        @field:NotEmpty
        val title: String,
        @field:NotEmpty
        val body: String,
        @field:NotEmpty
        val tags: List<String>
)


data class PostCreatedRes(
        val id: String
)

data class PostRes(
        val post: PostDto
)

data class PostDeleteRes(
        val id: String
)


data class UpdatePostReq(
        @field:NotBlank
        val id: String,
        val title: String?,
        val body: String?,
        val tags: List<String>?
)

data class UpdatePostRes(
        val id: String
)


data class UpsertTagReq(
        @field:NotBlank
        val tag: String
)

data class UpsertTagRes(
        val tag: String
)


data class DeleteTagRes(
        val tag: String
)

data class FindTagRes(
        val tag: String
)


data class PostDynamicListRes(
        val page: Int,
        val perPage: Int,
        val posts: List<PostDto>
)

data class CreateSeriesReq(
        @field:NotBlank
        val title: String,
        val body: String = "",
        val postIdList: List<String> = listOf()
)


data class CreatedSeriesRes(
        val id: UUID
)

data class GetSeriesRes(
        val series: SeriesDetailDto
)

data class DeleteSeriesRes(
        val seriesId: String
)

data class UpdateSeriesReq(
        @field:NotBlank
        val id: String,
        val title: String? = null,
        val body: String? = null,
        val postIdList: List<String>? = null
)

data class UpdateSeriesRes(
        val id: String
)

data class ListSeriesRes(
        val seriesList: List<SeriesDto>,
        val perPage: Int,
        val page: Int
)

data class TagStatisticsRes(
        val tags: List<TagStatistics>
)

@RestV2
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class PostController(
        val postService: PostService
) {

    @PostMapping("/post")
    @Secured("ROLE_USER")
    fun createPost(
            @RequestBody @Valid
            req: PostCreateReq
    ): PostCreatedRes {
        var ret = postService.createPost(CreatePostDto(title = req.title, body = req.body, tags = req.tags))
        return PostCreatedRes(id = ret.id)
    }

    @GetMapping("/post/{id}")
    fun getPost(@PathVariable id: String): PostRes {
        var post = postService.fetchPost(id)
        return PostRes(post = post)
    }

    @Secured("ROLE_USER")
    @PutMapping("/post")
    fun updatePost(@RequestBody @Valid req: UpdatePostReq): UpdatePostRes {
        var updatedPostId = postService.updatePost(
                PostUpdateDto(
                        id = req.id,
                        title = req.title,
                        body = req.body,
                        tags = req.tags
                )
        )
        return UpdatePostRes(id = updatedPostId.toString())
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/post/{id}")
    fun deletePost(@PathVariable id: String): PostDeleteRes {
        postService.deletePost(id = id)
        return PostDeleteRes(id = id)
    }

    @Secured("ROLE_USER")
    @PostMapping("/post/tag")
    fun upsertTag(@RequestBody @Valid upsertTagReq: UpsertTagReq): UpsertTagRes {
        postService.upsertTag(upsertTagReq.tag)
        return UpsertTagRes(tag = upsertTagReq.tag)
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/post/tag/{tag}")
    fun deleteTag(@PathVariable tag: String): DeleteTagRes {
        postService.deleteTag(tag)
        return DeleteTagRes(tag = tag)
    }

    @GetMapping("/post/tag/{tag}")
    fun findTag(@PathVariable tag: String): FindTagRes {
        var tagDto = postService.findTag(tag)
        return FindTagRes(tag = tagDto.tagName)
    }

    @GetMapping("/post/list")
    fun listPost(
            @RequestParam(defaultValue = "1") page: Int,
            @RequestParam(defaultValue = "10") perPage: Int = 10,
            @RequestParam(defaultValue = "All") tags: List<String>,
            @RequestParam title: String?
    ): PostDynamicListRes {
        var lists = postService.listDynamicPost(
                PostCondition(
                        tagNames = tags,
                        title = title
                ),
                PageRequest.of(page - 1, perPage)
        )

        return PostDynamicListRes(
                page = page,
                perPage = perPage,
                posts = lists
        )
    }

    @Secured("ROLE_USER")
    @PostMapping("/post/series")
    fun createSeries(@RequestBody @Valid req: CreateSeriesReq): CreatedSeriesRes {
        var id = postService.createSeries(
                CreateSeriesDto(
                        title = req.title,
                        body = req.body,
                        postIds = req.postIdList.map { UUID.fromString(it) }
                )
        )
        return CreatedSeriesRes(id = id)
    }

    @GetMapping("/post/series-with-post")
    fun getSeries(@RequestParam seriesId: String): GetSeriesRes {
        val dto = postService.fetchSeries(UUID.fromString(seriesId))
        return GetSeriesRes(series = dto)
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/post/series")
    fun deleteSeries(@RequestParam seriesId: String): DeleteSeriesRes {
        postService.deleteSeries(UUID.fromString(seriesId))
        return DeleteSeriesRes(seriesId = seriesId)
    }

    @Secured("ROLE_USER")
    @PutMapping("/post/series")
    fun updateSeries(@RequestBody @Valid req: UpdateSeriesReq): UpdateSeriesRes {
        var id = postService.updateSeries(
                UpdateSeriesDto(
                        id = UUID.fromString(req.id),
                        title = req.title,
                        body = req.body,
                        postIds = req.postIdList?.map { UUID.fromString(it) }
                )
        )
        return UpdateSeriesRes(id = id.toString())
    }

    @GetMapping("/post/series/list")
    fun listSeries(
            @RequestParam(defaultValue = "1") page: Int,
            @RequestParam(defaultValue = "10") perPage: Int
    ): ListSeriesRes {
        val seriesList = postService.listSeries(PageRequest.of(page - 1, perPage))
        return ListSeriesRes(seriesList = seriesList, perPage = perPage, page = page)
    }

    @GetMapping("/post/tag/statistics")
    fun getTagStatistics(): TagStatisticsRes {
        var statistics = postService.getTagStatistics()
        return TagStatisticsRes(tags = statistics)
    }

}


