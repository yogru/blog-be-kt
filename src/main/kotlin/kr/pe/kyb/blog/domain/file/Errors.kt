package kr.pe.kyb.blog.domain.file

import kr.pe.kyb.blog.infra.error.HttpErrorRes
import kr.pe.kyb.blog.infra.error.ServiceException


class NotFoundFileEntity(
        fileId: String
) : ServiceException("$fileId 존재하지 않는 파일", HttpErrorRes.NotFound)


class NotFoundFile(
        originalName: String
) : ServiceException("$originalName 존재하지 않는 파일", HttpErrorRes.NotFound)