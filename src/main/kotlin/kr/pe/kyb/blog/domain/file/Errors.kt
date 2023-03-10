package kr.pe.kyb.blog.domain.file

import kr.pe.kyb.blog.infra.error.ControllerException
import kr.pe.kyb.blog.infra.error.HttpErrorRes
import kr.pe.kyb.blog.infra.error.ServiceException


class NotFoundFileEntity(
        fileId: String
) : ServiceException("$fileId 존재하지 않는 파일", HttpErrorRes.NotFound)


class NotFoundFile(
        fileId: String
) : ServiceException("$fileId 존재하지 않는 파일", HttpErrorRes.NotFound)

class DeleteFailedFile(
        fileId: String
) : ServiceException("$fileId 파일 삭제에 실패")


class InvalidFileStatusException(
        status: FileStatus
) : ServiceException("${status.toString()} 상태")


class NotFoundLocalFile :
        ControllerException("서버 로컬파일 읽기 실패", HttpErrorRes.NotFound)

class InvalidMultipartFile(message: String) :
        ControllerException(message, HttpErrorRes.BadRequest)

