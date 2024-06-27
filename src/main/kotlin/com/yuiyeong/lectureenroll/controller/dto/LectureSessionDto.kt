package com.yuiyeong.lectureenroll.controller.dto

import com.yuiyeong.lectureenroll.domain.LectureSession
import java.time.format.DateTimeFormatter

data class LectureSessionDto(
    val id: Long,
    val lectureName: String,
    val lectureInstructorName: String,
    val sessionStart: String,
    val sessionEnd: String
) {
    companion object {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fun from(lectureSession: LectureSession): LectureSessionDto =
            LectureSessionDto(
                id = lectureSession.id,
                lectureName = lectureSession.lecture.name,
                lectureInstructorName = lectureSession.lecture.instructorName,
                sessionStart = lectureSession.lecturePeriod.start.format(formatter),
                sessionEnd = lectureSession.lecturePeriod.end.format(formatter)
            )
    }
}