package kr.pe.kyb.blog.infra.error

enum class HttpErrorRes(
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


data class SimpleErrorResponse(
    val statusCode: Int,
    val message: String,
    var detail: String? = null
)

open class LayerException(
    private val httpRes: HttpErrorRes,
    private val _message: String?
) : RuntimeException() {
    val response: SimpleErrorResponse
        get() = SimpleErrorResponse(
            this.statusCode,
            this.message,
            this.stackTrace[0].toString()
        )

    override val message: String
        get() {
            if (this._message == null) {
                return this.httpRes.defaultMessage
            }
            return this._message
        }
    private val statusCode: Int
        get() = this.httpRes.httpStatusCode
}


class DomainException(message: String) :
    LayerException(HttpErrorRes.InternalServerError, message)


open class ServiceException(
    message: String,
    errorRes: HttpErrorRes = HttpErrorRes.InternalServerError,
) : LayerException(errorRes, message)


class ControllerException(
    message: String,
    errorRes: HttpErrorRes = HttpErrorRes.InternalServerError
) : LayerException(errorRes, message)