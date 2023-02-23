package kr.pe.kyb.blog.infra.error

enum class HttpCode(
    val httpStatusCode: Int,
    val defaultMessage: String
) {
    BadRequest(400, "잘못된 요청 입니다."),
    Unauthorized(401, "유효한 자격증명이 존재하지 않습니다."),
    Forbidden(403, "접근 권한이 없습니다."),
    NotFound(404, "존재하지 않습니다."),
    Conflict(409, "요청이 충돌하여 완료할 수 없습니다."),
    InternalServerError(500, "서버 내부 에러 입니다.")
}


data class ErrorCode(
    val httpErrorResCode: HttpCode,
    var _message: String?
) {
    var message
        get() = this._message ?: httpErrorResCode.defaultMessage
        set(newMessage: String) {
            this._message = newMessage
        }

    val statusCode
        get() = this.httpErrorResCode.httpStatusCode
}