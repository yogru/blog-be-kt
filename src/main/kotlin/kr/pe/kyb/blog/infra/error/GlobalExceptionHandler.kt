package kr.pe.kyb.blog.infra.error

import kr.pe.kyb.blog.infra.config.ConstantValue
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(LayerException::class)
    fun handleCustomException(e: LayerException): ResponseEntity<SimpleErrorResponse> {
        val res = e.response
        if (!ConstantValue.IS_DEBUG) {
            res.detail = ""
        }
        return ResponseEntity.status(res.statusCode).body(res)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleIllegalArgumentException(e: MethodArgumentNotValidException): ResponseEntity<SimpleErrorResponse> {
        return ResponseEntity.status(HttpErrorRes.BadRequest.httpStatusCode).body(
            SimpleErrorResponse(
                statusCode = HttpErrorRes.BadRequest.httpStatusCode,
                message = e.bindingResult.allErrors[0].defaultMessage.toString()
            )
        )
    }


}