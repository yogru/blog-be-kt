package kr.pe.kyb.blog.infra.error

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ControllerException::class)
    fun handleCustomException(e: ControllerException): ResponseEntity<ControllerErrorResponse> {
        val res = e.response
        return ResponseEntity.status(res.statusCode).body(res)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleIllegalArgumentException(e: MethodArgumentNotValidException): ResponseEntity<ControllerErrorResponse> {
        return ResponseEntity.status(HttpCode.BadRequest.httpStatusCode).body(
            ControllerErrorResponse(
                statusCode = HttpCode.BadRequest.httpStatusCode,
                message = e.bindingResult.allErrors[0].defaultMessage.toString()
            )
        )
    }


//    @ExceptionHandler(Exception::class)
//    fun handleException(e: Exception): ResponseEntity<ControllerErrorResponse> {
//        return ResponseEntity.status(500)
//            .body(ControllerErrorResponse(statusCode = 500, e.toString(), e.toString()))
//    }
}