package kr.pe.kyb.blog.domain.file.repository

import kr.pe.kyb.blog.domain.file.DeleteFailedFile
import kr.pe.kyb.blog.domain.file.FileEntity
import kr.pe.kyb.blog.domain.file.InvalidFileStatusException
import kr.pe.kyb.blog.domain.file.NotFoundFile
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

@Component
class FileSystemRepository {

    fun saveFile(entity: FileEntity, data: ByteArray) {
        fun makeBasePath() {
            var path = Paths.get(entity.basePath)
            Files.createDirectories(path)
        }
        makeBasePath()
        val fileOutputStream = FileOutputStream(File(entity.dir))
        fileOutputStream.write(data)
        fileOutputStream.close()
        entity.saveFile()
    }

    fun loadFile(entity: FileEntity): ByteArray {
        if (!entity.isFileValid()) {
            throw InvalidFileStatusException(entity.status)
        }
        var file = File(entity.dir)
        if (!file.exists()) {
            throw NotFoundFile(entity.originName)
        }
        return file.readBytes()
    }

    fun delete(entity: FileEntity) {
        var file = File(entity.dir)
        if (!file.exists()) {
            entity.setNoneFile()
            throw NotFoundFile(entity.originName)
        }
        var deleted = file.delete()
        if (!deleted) {
            entity.deleteFail()
            throw DeleteFailedFile(entity.id.toString())
        }
        entity.deleted()
    }
}