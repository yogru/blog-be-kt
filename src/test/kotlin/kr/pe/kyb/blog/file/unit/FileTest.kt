package kr.pe.kyb.blog.file.unit


import kr.pe.kyb.blog.domain.file.FileService
import kr.pe.kyb.blog.domain.file.UploadFileDto
import kr.pe.kyb.blog.infra.error.DomainException
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


@MyTest
@DisplayName("unit: files")
@Transactional(readOnly = true)
class FileTest {


    @Autowired
    lateinit var fileService: FileService


    @Test
    @Transactional
    fun crd() {
        val bytes = "TestHello".toByteArray()
        val fileId = fileService.uploadFile(
            UploadFileDto(
                contentType = "text/plain",
                byteArray = bytes,
                originFilename = "test",
                ext = "txt",
            )
        )
        val fileDto = fileService.readFile(fileId)
        Assertions.assertEquals(fileId, fileDto.fileId)
        Assertions.assertEquals(true, bytes.contentEquals(fileDto.byteArray))
        fileService.deleteFile(fileId)
        Assertions.assertThrows(DomainException::class.java) {
            fileService.readFile(fileId)
        }
    }

}