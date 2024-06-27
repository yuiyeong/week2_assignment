package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.EnrollmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EnrollmentJpaRepository : JpaRepository<EnrollmentEntity, Long> {
    fun findAllByStudentId(studentId: Long): List<EnrollmentEntity>

    @Query("SELECT e FROM EnrollmentEntity e WHERE e.lectureSession.lecture.id = :lectureId AND e.student.id = :studentId")
    fun findAllByLectureIdAndStudentId(lectureId: Long, studentId: Long): List<EnrollmentEntity>
}
