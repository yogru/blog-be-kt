package kr.pe.kyb.blog.domain.file.repository

import kr.pe.kyb.blog.domain.file.FileEntity
import kr.pe.kyb.blog.domain.file.NotFoundFile
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

@Component
class FileSystemRepository {

    fun saveFile(fileEntity: FileEntity, data: ByteArray) {
        fun makeBasePath(fileEntity: FileEntity) {
            println(fileEntity.basePath)
            var path = Paths.get(fileEntity.basePath)
            Files.createDirectories(path)
        }

        makeBasePath(fileEntity)
        val fileOutputStream = FileOutputStream(File(fileEntity.dir))
        fileOutputStream.write(data)
        fileOutputStream.close()
        fileEntity.saveFile()
    }

    fun loadFile(fileEntity: FileEntity): ByteArray {
        var file = File(fileEntity.dir)
        if (!file.exists()) {
            throw NotFoundFile(fileEntity.originName)
        }
        return file.readBytes()
    }
}