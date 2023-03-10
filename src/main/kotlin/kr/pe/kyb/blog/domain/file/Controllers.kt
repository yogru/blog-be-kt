package kr.pe.kyb.blog.domain.file

import jakarta.servlet.http.HttpServletResponse
import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.file.SpringFileUtils
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class UploadFileRes(
        val fileId: String
)

@RestV2
class FileController(
        val fileService: FileService
) {

    @Secured("ROLE_USER")
    @PostMapping("/file")
    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileRes {
        var summary = SpringFileUtils.readMultipartFileSummary(file)
        var fileId = fileService.uploadFile(UploadFileDto(
                ext = summary.ext,
                contentType = summary.contentType,
                originFilename = summary.originName,
                byteArray = summary.byteArray
        ))
        return UploadFileRes(fileId = fileId.toString())
    }

    @Secured("permitAll")
    @GetMapping("/file/{fileId}")
    fun getFile(@PathVariable fileId: String, response: HttpServletResponse) {
        var fileDto = fileService.readFile(UUID.fromString(fileId))
        response.contentType = fileDto.contentType
        response.setContentLength(fileDto.byteArray.size)
        response.addHeader("Content-Disposition", "attachment; filename=${fileDto.originFilename}")
        response.outputStream.write(fileDto.byteArray)
        response.outputStream.flush()
    }

}