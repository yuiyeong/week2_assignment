package com.yuiyeong.lectureenroll

import com.yuiyeong.lectureenroll.controller.dto.ErrorResult
import com.yuiyeong.lectureenroll.service.ExceptionTranslationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ApiControllerAdvice(
    private val exceptionTranslationService: ExceptionTranslationService
) : ResponseEntityExceptionHandler() {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResult> {
        val error = exceptionTranslationService.translateException(e)
        return ResponseEntity(
            ErrorResult(error.message),
            error.status,
        )
    }
}