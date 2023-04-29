package kr.pe.kyb.blog.domain.file.infra

import kr.pe.kyb.blog.domain.file.FileEntity
import kr.pe.kyb.blog.infra.file.MinIoWrapper
import org.springframework.stereotype.Component


@Component
class MinIoObjectStorage(
    private val minIoWrapper: MinIoWrapper
) {
    fun upload(fileEntity: FileEntity, data: ByteArray) {
        minIoWrapper.putObject(
            fileEntity.id.toString(),
            data.inputStream(),
            fileEntity.contentType
        )
        fileEntity.savedFile()
    }

    fun download(fileEntity: FileEntity): ByteArray {
        fileEntity.checkValid()
        return minIoWrapper.getObject(fileEntity.id.toString())
    }


    fun delete(fileEntity: FileEntity) {
        fileEntity.checkValid()
        minIoWrapper.removeObject(fileEntity.id.toString())
    }

    fun getBucketName(): String {
        return this.minIoWrapper.configValues.defaultBucketName
    }
}