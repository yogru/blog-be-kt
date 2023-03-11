package kr.pe.kyb.blog.domain.file

import jakarta.servlet.http.HttpServletResponse
import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.file.SpringFileUtils
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

data class UploadFileRes(
        val fileId: String
)

@RestV2
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
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

    // 이거 위험한 상태. 파일 마다 권한 검사 추가해야함
    @GetMapping("/file/static/{fileId}")
    fun getFile(@PathVariable fileId: String, response: HttpServletResponse) {
        var fileDto = fileService.readFile(UUID.fromString(fileId))
        response.contentType = fileDto.contentType
        response.setContentLength(fileDto.byteArray.size)
        val encodedFilename = URLEncoder.encode(fileDto.originFilename, StandardCharsets.UTF_8)
        response.addHeader("Content-Disposition", "attachment; filename=${encodedFilename}")
        response.outputStream.write(fileDto.byteArray)
        response.outputStream.flush()
    }

}