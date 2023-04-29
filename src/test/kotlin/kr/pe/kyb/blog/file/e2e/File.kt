package kr.pe.kyb.blog.file.e2e

import kr.pe.kyb.blog.domain.file.FileService
import kr.pe.kyb.blog.domain.file.UploadFileRes
import kr.pe.kyb.blog.mock.MyTest
import kr.pe.kyb.blog.mock.api.MockMvcWrapper
import kr.pe.kyb.blog.mock.testUserIdString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


@MyTest
@DisplayName("e2e: files")
@Transactional(readOnly = true)
class File {

    @Autowired
    lateinit var mockMvcWrapper: MockMvcWrapper

    @Autowired
    lateinit var fileService: FileService


    @Test
    @WithMockUser(username = testUserIdString, roles = ["USER"])
    @Transactional
    fun crd() {
        val bytes = "테스트".toByteArray()
        val originName = "test.txt"
        val contentType = "text/plain"
        val res = mockMvcWrapper
            .withFormFile(
                "/file",
                listOf(MockMultipartFile("file", originName, contentType, bytes))
            )
            .withBearerToken()
            .request(UploadFileRes::class.java)

        Assertions.assertNotNull(res)
        Assertions.assertNotNull(res.fileId)

        val readRes = mockMvcWrapper
            .withGetHeader("/file/static/${res.fileId}")
            .response()

        Assertions.assertEquals(HttpStatus.OK.value(), readRes.status)
        Assertions.assertEquals(bytes.size, readRes.contentLength)
        Assertions.assertEquals(true, bytes.contentEquals(readRes.contentAsByteArray))
        Assertions.assertEquals(contentType, readRes.contentType)

        // 파일 삭제 e2e 안만듬 나중에 보안 관련 확실히 하면 만들것
        fileService.deleteFile(UUID.fromString(res.fileId))
    }

}