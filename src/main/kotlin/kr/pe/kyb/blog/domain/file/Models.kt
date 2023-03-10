package kr.pe.kyb.blog.domain.file

import jakarta.persistence.*
import kr.pe.kyb.blog.infra.persistence.JPABaseEntity
import org.hibernate.annotations.GenericGenerator
import java.util.*
import kotlin.collections.ArrayList

enum class FileStatus {
    NoneFile,
    Normal(),
    Deleted(),
    DeleteFailed()
}

@Entity
class FileEntity(
        contentType: String,
        basePath: String,
        originName: String,
        ext: String,
        size: Long,
        status: FileStatus = FileStatus.NoneFile,
) : JPABaseEntity() {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID()

    @Column(length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    var status: FileStatus = status

    @Column(length = 255, nullable = false)
    var dir: String = "$basePath/$id.$ext"

    @Column(length = 255, nullable = false)
    val contentType: String = contentType

    @Column(length = 255, nullable = false)
    var ext: String = ext

    @Column(length = 255, nullable = false)
    var originName: String = originName

    @Column(nullable = false)
    val size: Long = size


    fun saveFile() {
        status = FileStatus.Normal
    }

    fun deleteFail() {
        this.status = FileStatus.DeleteFailed
    }

    fun setNoneFile() {
        this.status = FileStatus.NoneFile
    }

    fun deleted() {
        this.status = FileStatus.Deleted
    }

    fun isFileValid(): Boolean {
        return this.status == FileStatus.Normal
    }

    val basePath: String
        get() {
            val pathTokens = dir.split("/")
            var ret = pathTokens[0]
            for (i in 1 until pathTokens.lastIndex) {
                ret += "/${pathTokens[i]}"
            }
            return ret
        }
}