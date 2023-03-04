package kr.pe.kyb.blog.domain.post

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import kr.pe.kyb.blog.infra.anotation.RestV2
import org.springframework.web.bind.annotation.*
import java.util.UUID

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

@RestV2
class PostController(
    val postService: PostService
) {
    @PostMapping("/post")
    fun createPost(
        @RequestBody @Valid
        req: PostCreateReq
    ): PostCreatedRes {
        var ret = postService.createPost(CreatePostDto(title = req.title, body = req.body, tags = req.tags))
        return PostCreatedRes(id = ret.id)
    }

    @GetMapping("/post/{id}")
    fun getPost(@PathVariable id: String): PostRes {
        var post = postService.findPost(id)
        return PostRes(post = post)
    }

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

    @DeleteMapping("/post/{id}")
    fun deletePost(@PathVariable id: String): PostDeleteRes {
        postService.deletePost(id = id)
        return PostDeleteRes(id = id)
    }

    @PostMapping("/post/tag")
    fun upsertTag(@RequestBody @Valid upsertTagReq: UpsertTagReq): UpsertTagRes {
        postService.upsertTag(upsertTagReq.tag)
        return UpsertTagRes(tag = upsertTagReq.tag)
    }

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

}


