package kr.pe.kyb.blog.domain.file

import kr.pe.kyb.blog.domain.file.infra.MinIoObjectStorage
import kr.pe.kyb.blog.domain.file.repository.FileEntityRepository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

class UploadFileDto(
    val ext: String,
    val contentType: String,
    val originFilename: String,
    val byteArray: ByteArray
)

class FileDto(
    val fileId: UUID,
    val ext: String,
    val contentType: String,
    val originFilename: String,
    val byteArray: ByteArray
)

@Service
@Transactional(readOnly = true)
class FileService(
    val fileEntityRepository: FileEntityRepository,
    val minIoObjectStorage: MinIoObjectStorage,
) {

    @Transactional
    fun uploadFile(dto: UploadFileDto): UUID {
        return FileEntity(
            contentType = dto.contentType,
            originName = dto.originFilename,
            basePath = minIoObjectStorage.getBucketName(),
            ext = dto.ext,
            size = dto.byteArray.size.toLong()
        ).let {
            minIoObjectStorage.upload(it, dto.byteArray)
            fileEntityRepository.persist(it)
            it.id
        }
    }

    fun readFile(fileId: UUID): FileDto {
        return fileEntityRepository.findById(fileId)
            .let { it ?: throw NotFoundFileEntity(fileId.toString()) }
            .let {
                FileDto(
                    fileId = it.id,
                    ext = it.ext,
                    contentType = it.contentType,
                    originFilename = it.originName,
                    byteArray = minIoObjectStorage.download(it)
                )
            }
    }

    fun deleteFile(fileId: UUID) {
        fileEntityRepository.findById(fileId)
            .let { it ?: throw NotFoundFileEntity(fileId.toString()) }
            .let {
                minIoObjectStorage.delete(it)
                it.deleted()
                fileEntityRepository.persist(it)
            }
    }


}