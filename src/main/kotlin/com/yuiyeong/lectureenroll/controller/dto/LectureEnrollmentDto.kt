package com.yuiyeong.lectureenroll.controller.dto

import com.yuiyeong.lectureenroll.service.SessionEnrollmentStatus

data class LectureEnrollmentDto(
    val lectureSessionId: Long,
    val isEnrolled: Boolean
) {
    companion object {
        fun from(enrollmentStatus: SessionEnrollmentStatus): LectureEnrollmentDto =
            LectureEnrollmentDto(enrollmentStatus.lectureSessionId, enrollmentStatus.isEnrolled)
    }
}