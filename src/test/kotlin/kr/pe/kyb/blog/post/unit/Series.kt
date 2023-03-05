package kr.pe.kyb.blog.post.unit

import kr.pe.kyb.blog.domain.post.*
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
    fun curd() {
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
                postIds = listOf(UUID.fromString(posts[1].id), UUID.fromString(posts[2].id))
            )
        )
        Assertions.assertNotNull(createdSeriesWithPostsId)
        val createdSeriesDtoWithPosts = postTestService.fetchSeries(createdSeriesWithPostsId)

        Assertions.assertIterableEquals(
            createdSeriesDtoWithPosts.posts.map { it.title }.sorted(),
            listOf(posts[1].title, posts[2].title)
        )

        // delete test
        postTestService.removeSeries(createdId)
        Assertions.assertThrows(NotFoundSeries::class.java) {
            postTestService.fetchSeries(createdId)
        }

        // update: post 목록 전부 없애기
        postTestService.updateSeries(
            UpdateSeriesDto(
                id = createdSeriesDtoWithPosts.id,
                postIds = listOf()
            )
        )
        var updatedSeries = postTestService.fetchSeries(createdSeriesDtoWithPosts.id)
        Assertions.assertEquals(updatedSeries.posts.size, 0)

        // update: 단순 값 변경 타이틀..
        val changedTitle = "changed.."
        postTestService.updateSeries(
            UpdateSeriesDto(updatedSeries.id, title = changedTitle)
        )

        updatedSeries = postTestService.fetchSeries(createdSeriesDtoWithPosts.id)
        Assertions.assertEquals(updatedSeries.title, changedTitle)

        val newPostIds = listOf(UUID.fromString(posts[2].id), UUID.fromString(posts[1].id))
        // update: 포스트 추가.
        postTestService.updateSeries(
            UpdateSeriesDto(
                updatedSeries.id,
                postIds = newPostIds
            )
        )

        updatedSeries = postTestService.fetchSeries(createdSeriesDtoWithPosts.id)
        println(newPostIds)
        println(updatedSeries.posts.map { it.id })
        Assertions.assertEquals(updatedSeries.posts.size, 2)
        Assertions.assertIterableEquals(
            updatedSeries.posts.map { it.id },
            newPostIds.map { it.toString() }
        )


    }
}