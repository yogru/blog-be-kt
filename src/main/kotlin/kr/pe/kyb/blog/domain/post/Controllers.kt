package kr.pe.kyb.blog.domain.post

import jakarta.validation.Valid
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

}