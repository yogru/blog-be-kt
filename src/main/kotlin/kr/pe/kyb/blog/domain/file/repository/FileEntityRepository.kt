package kr.pe.kyb.blog.domain.file.repository

import kr.pe.kyb.blog.domain.file.FileEntity
import kr.pe.kyb.blog.infra.persistence.JpaBaseRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface FileEntityRepositoryInterface : JpaRepository<FileEntity, UUID> {

}


@Repository
class FileEntityRepository(
        val jpaRepo: FileEntityRepositoryInterface
) : JpaBaseRepository() {

    fun findById(fileId: UUID): FileEntity? {
        return jpaRepo.findById(fileId)
                .let { if (it.isEmpty) null else it.get() }
    }
}