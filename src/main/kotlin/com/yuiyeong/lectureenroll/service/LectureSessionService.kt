package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.domain.LectureSession
import com.yuiyeong.lectureenroll.exception.LectureSessionNotFoundException
import com.yuiyeong.lectureenroll.exception.StudentNotFoundException
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LectureSessionService(
    private val studentRepository: StudentRepository,
    private val sessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository,
) {
    fun enroll(lectureSessionId: Long, studentId: Long): LectureSession {
    }}