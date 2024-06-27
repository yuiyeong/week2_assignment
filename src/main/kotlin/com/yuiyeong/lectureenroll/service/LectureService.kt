package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    fun getLectureEnrollmentStatuses(lectureId: Long, studentId: Long): List<SessionEnrollmentStatus> {
        val lectureSessions = lectureSessionRepository.findAllByLectureId(lectureId)
        val studentEnrollments = enrollmentRepository.findAllByLectureIdAndStudentId(lectureId, studentId)

        val enrollmentMap = studentEnrollments.associateBy { it.lectureSession.id }
        return lectureSessions.map { SessionEnrollmentStatus(it.id, enrollmentMap.containsKey(it.id)) }
    }
}

data class SessionEnrollmentStatus(
    val lectureSessionId: Long,
    val isEnrolled: Boolean
)