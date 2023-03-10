package kr.pe.kyb.blog.infra.file

import kr.pe.kyb.blog.domain.file.InvalidMultipartFile
import kr.pe.kyb.blog.domain.file.NotFoundLocalFile
import kr.pe.kyb.blog.infra.error.ControllerException
import kr.pe.kyb.blog.infra.error.HttpErrorRes
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.nio.file.Paths


class MultipartFileSummary(
        val originName: String,
        val size: Long,
        val contentType: String,
        val ext: String,
        val byteArray: ByteArray
)

class SpringFileUtils {

    companion object {
        fun readMultipartFileSummary(f: MultipartFile): MultipartFileSummary {
            if (f.isEmpty) {
                throw InvalidMultipartFile("파일이 업로드 되지 않았습니다.")
            }
            if (f.originalFilename == null) {
                throw InvalidMultipartFile("파일 이름이 존재 하지 않습니다")
            }
            val fileNames = f.originalFilename!!.split(".")
            return MultipartFileSummary(
                    originName = fileNames[0],
                    size = f.size,
                    contentType = f.contentType!!,
                    ext = fileNames[1],
                    byteArray = f.bytes
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