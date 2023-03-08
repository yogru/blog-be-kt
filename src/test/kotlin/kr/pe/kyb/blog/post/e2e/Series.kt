package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.*
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
        var series0 = mockMvcWrapper.withPostHeader(
                url = "/post/series",
                body = CreateSeriesReq(
                        title = "title",
                        body = "body",
                )
        ).withBearerToken().request(CreatedSeriesRes::class.java)
        Assertions.assertNotNull(series0.id)

        var series1 = mockMvcWrapper.withPostHeader(
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

        // read
        var getSeries = mockMvcWrapper
                .withGetHeader("/post/series-with-post?seriesId=${series1.id}")
                .request(GetSeriesRes::class.java)

        Assertions.assertEquals(getSeries.series.posts.size, 2)

        // delete
        mockMvcWrapper
                .withDeleteHeader("/post/series?seriesId=${series1.id}")
                .request(DeleteSeriesRes::class.java)

        val failDelete = mockMvcWrapper
                .withGetHeader("/post/series-with-post?seriesId=${series1.id}")
                .withBearerToken()
                .requestSimpleFail()
        Assertions.assertEquals(failDelete.statusCode, 404)


        val changedTitle = "changed..title"
        val changedPostIds = listOf(
                preparedPostIdList[1].toString(), // 중복.
                preparedPostIdList[2].toString(),
                preparedPostIdList[3].toString(),
        )

        mockMvcWrapper.withPutHeader("/post/series",
                UpdateSeriesReq(
                        id = series0.id.toString(),
                        title = changedTitle,
                        postIdList = changedPostIds
                ))
                .withBearerToken()
                .request(UpdateSeriesRes::class.java)


        getSeries = mockMvcWrapper
                .withGetHeader("/post/series-with-post?seriesId=${series0.id}")
                .request(GetSeriesRes::class.java)

        Assertions.assertEquals(getSeries.series.id, series0.id)
        Assertions.assertEquals(getSeries.series.title, changedTitle)
        Assertions.assertIterableEquals(
                getSeries.series.posts.map { it.id }.sorted(),
                changedPostIds.sorted()
        )
    }


    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun list() {

        for (i in 1..100) {
            mockMvcWrapper.withPostHeader(
                    url = "/post/series",
                    body = CreateSeriesReq(
                            title = "title$i",
                            body = "body$i",
                    )
            ).withBearerToken().request(CreatedSeriesRes::class.java)
        }

        var res = mockMvcWrapper.withGetHeader("/post/series/list")
                .request(ListSeriesRes::class.java)

        Assertions.assertEquals(res.seriesList.size, 10)

        res = mockMvcWrapper.withGetHeader("/post/series/list?page=11")
                .request(ListSeriesRes::class.java)

        Assertions.assertEquals(res.seriesList.size, 0)

    }


}