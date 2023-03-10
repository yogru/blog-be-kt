package kr.pe.kyb.blog.file.e2e

import kr.pe.kyb.blog.domain.file.UploadFileRes
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.testUserIdString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional


@MyTest
@DisplayName("e2e: files")
@Transactional(readOnly = true)
class File {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper


    @Test
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    @Transactional
    fun `파일 생성 조회`() {
        val bytes = "테스트".toByteArray()
        var res = mockMvcWrapper
                .withFormFile("/file",
                        listOf(MockMultipartFile("file", "test.txt", "text/plain", bytes)))
                .withBearerToken()
                .request(UploadFileRes::class.java)

        Assertions.assertNotNull(res)
        Assertions.assertNotNull(res.fileId)
    }

}