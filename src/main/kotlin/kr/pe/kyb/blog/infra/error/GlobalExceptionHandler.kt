package kr.pe.kyb.blog.infra.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ControllerException::class)
    fun handleCustomException(e: ControllerException): ResponseEntity<ControllerErrorResponse> {
        val res = e.response
        return ResponseEntity.status(res.statusCode).body(res)
    }
}