package com.yuiyeong.lectureenroll.domain

import java.time.LocalDateTime

class Enrollment(
    val id: Long,
    val student: Student,
    val lectureSession: LectureSession,
    val enrolledAt: LocalDateTime
)