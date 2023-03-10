package kr.pe.kyb.blog.domain.file

import kr.pe.kyb.blog.infra.anotation.RestV2
import kr.pe.kyb.blog.infra.file.SpringFileUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

data class UploadFileRes(
        val fileId: String
)

@RestV2
class FileController(
        val fileService: FileService
) {


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

}