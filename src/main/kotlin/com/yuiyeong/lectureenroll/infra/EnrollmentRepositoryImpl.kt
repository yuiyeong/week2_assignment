package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import com.yuiyeong.lectureenroll.infra.entity.EnrollmentEntity
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository
import org.springframework.transaction.UnexpectedRollbackException

@Repository
class EnrollmentRepositoryImpl(
    private val jpaRepository: EnrollmentJpaRepository
) : EnrollmentRepository {
    override fun findAllByStudentId(studentId: Long): List<Enrollment> =
        jpaRepository.findAllByStudentId(studentId)
            .map { it.toEnrollment() }

    override fun save(enrollment: Enrollment): Enrollment {
        try {
            return jpaRepository.saveAndFlush(enrollment.toEntity()).toEnrollment()
        } catch (e: DataIntegrityViolationException) {
            // unique constraints 에 의하여 발생
            if (e.cause is ConstraintViolationException) {
                val exception = AlreadyEnrolledException("이미 신청 완료한 강의입니다.")
                throw UnexpectedRollbackException(exception.notNullMessage, exception)
            }
            // constraints 에 의한게 아니면 rethrow
            throw e
        }
    }

    override fun deleteAll() = jpaRepository.deleteAll()
}

fun Enrollment.toEntity(): EnrollmentEntity = EnrollmentEntity(
    id = id,
    studentEntity = student.toEntity(),
    lectureSessionEntity = lectureSession.toEntity(),
    enrolledAt = enrolledAt
)

fun EnrollmentEntity.toEnrollment(): Enrollment = Enrollment(
    id = id,
    student = student.toStudent(),
    lectureSession = lectureSession.toLectureSession(),
    enrolledAt = enrolledAt
)