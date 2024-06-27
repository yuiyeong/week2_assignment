package com.yuiyeong.lectureenroll.exception

open class NotFoundException(entityName: String) : LectureEnrollmentException("해당되는 ${entityName}이 없습니다.")

class StudentNotFoundException : NotFoundException("학생")

class LectureSessionNotFoundException : NotFoundException("특강 세션")
