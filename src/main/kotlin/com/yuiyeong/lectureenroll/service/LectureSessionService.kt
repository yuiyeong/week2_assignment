package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.domain.LectureSession
import com.yuiyeong.lectureenroll.exception.LectureSessionNotFoundException
import com.yuiyeong.lectureenroll.exception.StudentNotFoundException
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.hibernate.exception.LockAcquisitionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LectureSessionService(
    private val studentRepository: StudentRepository,
    private val sessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository,
) {
    fun findAll(): List<LectureSession> {
        return sessionRepository.findAll()
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 20)
    fun enroll(sessionId: Long, studentId: Long): LectureSession {
        val session = sessionRepository.findOneWithLockById(sessionId) ?: throw LectureSessionNotFoundException()
        val student = studentRepository.findOneById(studentId) ?: throw StudentNotFoundException()
        enrollmentRepository.findAllByStudentId(student.id).also {
            session.addEnrollments(it)
        }

        val at = LocalDateTime.now()
        val enrollment = session.enroll(student, at)
        enrollmentRepository.save(enrollment)

        return sessionRepository.save(session)
    }
}