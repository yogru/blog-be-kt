package kr.pe.kyb.blog.domain.user

import kr.pe.kyb.blog.infra.error.HttpErrorRes
import kr.pe.kyb.blog.infra.error.ServiceException


class CreateFailExistEmail(
    email: String
) : ServiceException("이미 존재하는 이메일 $email")

class CreateFail : ServiceException("영속 계층 유저 생성 실패")


class NotFoundUser(email: String) : ServiceException("${email}를 찾을 수 없습니다", HttpErrorRes.NotFound)

class NotFoundUserDetail : ServiceException("현재 유저를 찾을 수 없습니다", HttpErrorRes.NotFound)
