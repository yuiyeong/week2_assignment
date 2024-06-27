package com.yuiyeong.lectureenroll.exception

open class LectureEnrollmentException(message: String) : Exception(message) {
    val notNullMessage: String
        get() = message!!
}