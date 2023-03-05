package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.CreatePostDto
import kr.pe.kyb.blog.domain.post.CreateSeriesDto
import kr.pe.kyb.blog.domain.post.CreatedPostDto
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.ArrayList


@MyTest
@DisplayName("unit: series")
class Series {

    @Autowired
    lateinit var postTestService: PostService

    var posts: MutableList<CreatedPostDto> = ArrayList()

    @BeforeEach
    @Transactional
    fun createTestPosts() {
        posts.clear()
        val createPosts: List<CreatePostDto> =
            listOf(
                CreatePostDto("post1", "body1", listOf("All")),
                CreatePostDto("post2", "body2", listOf("All", "test1")),
                CreatePostDto("post3", "body3", listOf("All", "test2", "test1")),
                CreatePostDto("post4", "body4", listOf("All", "test3")),
            )
        for (createPost in createPosts) {
            posts.add(postTestService.createPost(createPost))
        }
    }

    @Test
    @Transactional
    fun createSeries() {
        // 단순 타이틀만 있는 시리즈 생성
        val title = "title."
        val createdId = postTestService.createSeries(CreateSeriesDto(title))
        Assertions.assertNotNull(createdId)
        val createdSeriesDto = postTestService.fetchSeries(createdId)
        Assertions.assertEquals(createdSeriesDto.title, title)

        // 존재하는 포스트와 연결 되어서 생성
        val createdSeriesWithPostsId = postTestService.createSeries(
            CreateSeriesDto(
                title,
                postIdList = listOf(UUID.fromString(posts[1].id), UUID.fromString(posts[2].id))
            )
        )
        Assertions.assertNotNull(createdSeriesWithPostsId)
        val createdSeriesDtoWithPosts = postTestService.fetchSeries(createdSeriesWithPostsId)

        Assertions.assertIterableEquals(
            createdSeriesDtoWithPosts.posts.map { it.title }.sorted(),
            listOf(posts[1].title, posts[2].title)
        )

    }
}