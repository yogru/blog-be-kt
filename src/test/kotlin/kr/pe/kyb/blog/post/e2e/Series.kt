package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.CreatePostDto
import kr.pe.kyb.blog.domain.post.CreateSeriesReq
import kr.pe.kyb.blog.domain.post.CreatedSeriesRes
import kr.pe.kyb.blog.domain.post.PostService
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.testUserIdString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


@MyTest
@DisplayName("e2e: Series")
class Series {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Autowired
    lateinit var postTestService: PostService


    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun curd() {
        fun preparePosts(): List<UUID> {
            var ret = ArrayList<UUID>()
            for (i in 1..10) {
                var res = postTestService.createPost(
                        CreatePostDto(
                                "title$i",
                                "body$i",
                                tags = listOf("All")
                        )
                )
                ret.add(UUID.fromString(res.id))
            }
            return ret
        }

        val preparedPostIdList = preparePosts()
        // post 없이..
        var res = mockMvcWrapper.withPostHeader(
                url = "/post/series",
                body = CreateSeriesReq(
                        title = "title",
                        body = "body",
                )
        ).withBearerToken().request(CreatedSeriesRes::class.java)
        Assertions.assertNotNull(res.id)

        res = mockMvcWrapper.withPostHeader(
                url = "/post/series",
                body = CreateSeriesReq(
                        title = "title",
                        body = "body",
                        postIdList = listOf(
                                preparedPostIdList[1].toString(),
                                preparedPostIdList[1].toString(), // 중복.
                                preparedPostIdList[3].toString()
                        )
                )
        ).withBearerToken().request(CreatedSeriesRes::class.java)

//        Assertions.assertEquals(r)

//        val getSeries = mockMvcWrapper.withGetHeader( "/api/v2/post/series")

    }

}