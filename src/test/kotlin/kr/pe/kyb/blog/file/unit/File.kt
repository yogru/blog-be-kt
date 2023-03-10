package kr.pe.kyb.blog.file.unit


import kr.pe.kyb.blog.domain.file.FileEntity
import kr.pe.kyb.blog.domain.file.FileService
import kr.pe.kyb.blog.domain.file.FileStatus
import kr.pe.kyb.blog.domain.file.UploadFileDto
import kr.pe.kyb.blog.domain.file.repository.FileSystemRepository
import kr.pe.kyb.blog.mock.MyTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


@MyTest
@DisplayName("unit: files")
@Transactional(readOnly = true)
class File {


    @Autowired
    lateinit var fileService: FileService

    @Test
    @Transactional
    fun fileSystemRepositoryTest() {
        var bytes = "TestHello".toByteArray()
        var fileId = fileService.uploadFile(UploadFileDto(
                contentType = "text",
                byteArray = bytes,
                originFilename = "test",
                ext = "txt",
        ))

        var fileDto = fileService.readFile(fileId)
        Assertions.assertEquals(fileId, fileDto.fileId)
        Assertions.assertEquals(true, bytes.contentEquals(fileDto.byteArray))

    }

}