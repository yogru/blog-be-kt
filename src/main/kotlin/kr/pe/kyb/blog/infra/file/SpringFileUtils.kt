package kr.pe.kyb.blog.infra.file

import kr.pe.kyb.blog.infra.error.ControllerException
import kr.pe.kyb.blog.infra.error.HttpErrorRes
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.nio.file.Paths


class NotFoundLocalFile :
        ControllerException("서버 로컬파일 읽기 실패", HttpErrorRes.NotFound)


data class MultipartFileSummary(
        val originName: String,
        val size: Long,
        val contentType: String
)

class SpringFileUtils {

    companion object {
        fun readMultipartFileSummary(f: MultipartFile): MultipartFileSummary {
            val originName = f.originalFilename
            val size = f.size
            val contentType = f.contentType
            return MultipartFileSummary(
                    originName = originName!!,
                    size = size,
                    contentType = contentType!!
            )
        }

        fun downloadServerLocalFile(path: String): ResponseEntity<Resource> {
            val path: Path = Paths.get(path)
            val resource: Resource = UrlResource(path.toUri())
            if (!resource.exists()) {
                throw NotFoundLocalFile()
            }
            val headers = HttpHeaders()
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
            return ResponseEntity.ok().headers(headers).body(resource)
        }
    }
}