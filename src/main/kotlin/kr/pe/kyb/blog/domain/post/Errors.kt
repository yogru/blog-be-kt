package kr.pe.kyb.blog.domain.post

import kr.pe.kyb.blog.infra.error.HttpErrorRes
import kr.pe.kyb.blog.infra.error.ServiceException

class UnremovableTagException(tagName: String) : ServiceException("${tagName}는 지울 수 없는 태그입니다.")

class NotFoundTag(tagName: String) : ServiceException("${tagName}은 존재 하지 않는 태그 입니다.", HttpErrorRes.NotFound)

class NotFoundPost(postId: String) : ServiceException("${postId}은 존재 하지 않는 게시물 입니다.", HttpErrorRes.NotFound)