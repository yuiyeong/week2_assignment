package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import com.yuiyeong.lectureenroll.exception.LectureEnrollmentException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException

@Service
class ExceptionTranslationService {
    fun translateException(e: Exception): Error {
        return when (e) {
            is UnexpectedRollbackException -> translateUnexpectedRollbackException(e)
            is LectureEnrollmentException -> Error(HttpStatus.BAD_REQUEST, e.notNullMessage)
            else -> Error(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 에러가 발생했습니다.")
        }
    }

    private fun translateUnexpectedRollbackException(e: UnexpectedRollbackException): Error {
        var cause = e.cause
        while (cause != null) {
            if (cause is AlreadyEnrolledException) {
                return Error(HttpStatus.BAD_REQUEST, cause.notNullMessage)
            }
            cause = e.cause
        }
        return Error(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 에러가 발생했습니다.")
    }
}

data class Error(val status: HttpStatus, val message: String)