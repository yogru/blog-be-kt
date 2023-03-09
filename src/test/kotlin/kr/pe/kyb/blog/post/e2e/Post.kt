package kr.pe.kyb.blog.post.e2e

import kr.pe.kyb.blog.domain.post.*
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.api.WithUser
import kr.pe.kyb.blog.mock.testUserIdString

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.*


@MyTest
@DisplayName("e2e: Post")
class Post {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper


    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun curd() {
        val title = "test_post"
        val body = "test_body"
        val tags = listOf("All")

        val res = mockMvcWrapper
                .withPostHeader("/post", PostCreateReq(title = title, body = body, tags = tags))
                .withBearerToken()
                .request(PostCreatedRes::class.java)


        val res1 = mockMvcWrapper
                .withPostHeader("/post", PostCreateReq(title = title, body = body, tags = tags))
                .withBearerToken()
                .request(PostCreatedRes::class.java)

        // Create
        Assertions.assertNotNull(res.id)
        Assertions.assertNotNull(res1.id)
        Assertions.assertNotEquals(res.id, res1.id)

        // Read
        var foundUser1 = mockMvcWrapper
                .withGetHeader("/post/${res.id}")
                .withBearerToken()
                .request(PostRes::class.java)

        Assertions.assertEquals(foundUser1.post.title, title)
        Assertions.assertEquals(foundUser1.post.body, body)
        Assertions.assertIterableEquals(foundUser1.post.tags.sorted(), tags.sorted())

        val changedTitle = "changed..title"
        val changedBody = "changed...body"
        val changedTags = listOf("All", "test1", "test2")
        // update
        mockMvcWrapper.withPutHeader("/post", UpdatePostReq(res.id, changedTitle, changedBody, changedTags))
                .withBearerToken()
                .request(UpdatePostRes::class.java)


        foundUser1 = mockMvcWrapper
                .withGetHeader("/post/${res.id}")
                .withBearerToken()
                .request(PostRes::class.java)


        Assertions.assertEquals(foundUser1.post.title, changedTitle)
        Assertions.assertEquals(foundUser1.post.body, changedBody)
        Assertions.assertIterableEquals(foundUser1.post.tags.sorted(), changedTags.sorted())

        // delete
        val deletedPostId = mockMvcWrapper
                .withDeleteHeader("/post/${foundUser1.post.id}")
                .withBearerToken().request(PostDeleteRes::class.java)

        val foundDeletedPost = mockMvcWrapper
                .withGetHeader("/post/${deletedPostId.id}")
                .withBearerToken()
                .requestSimpleFail()
        Assertions.assertEquals(foundDeletedPost.statusCode, 404)
    }


    @Test
    @Transactional
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    fun listing() {
        val testTagNames = listOf(
                "react", "database", "it", "kotlin", "spring", "spring-boot",
                "os", "network", "study", "etc", "movie"
        )

        fun prepareMockData() {
            for (tag in testTagNames) {
                mockMvcWrapper
                        .withPostHeader("/post/tag", UpsertTagRes(tag = tag))
                        .withBearerToken()
                        .request(UpsertTagRes::class.java)
            }

            for (i in 1..100) {
                mockMvcWrapper
                        .withPostHeader(
                                "/post", PostCreateReq(
                                title = "title$i",
                                body = "body$i",
                                tags = listOf(
                                        "All",
                                        testTagNames[i % testTagNames.size],
                                        testTagNames[(i + 1) % testTagNames.size]
                                )
                        )
                        )
                        .withBearerToken()
                        .request(PostCreatedRes::class.java)
            }
        }
        prepareMockData()

        val list = mockMvcWrapper
                .withGetHeader("/post/list?page=1&&tags=${testTagNames[0]}&&tags=${testTagNames[1]}")
                .withBearerToken()
                .request(PostDynamicListRes::class.java)

        Assertions.assertEquals(list.posts.size, 10)

        Assertions.assertEquals(
                list.posts
                        .filter { it.tags.contains(testTagNames[0]) || it.tags.contains(testTagNames[1]) }.size,
                10
        )

        val tagStatistics = mockMvcWrapper
                .withGetHeader("/post/tag/statistics")
                .withBearerToken()
                .request(TagStatisticsRes::class.java)

        Assertions.assertEquals(tagStatistics.tags[0].tag, "All")
        Assertions.assertEquals(tagStatistics.tags[0].count, 100)
        Assertions.assertEquals(tagStatistics.tags.size, testTagNames.size + 1)
    }
}