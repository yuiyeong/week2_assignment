package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.EnrollmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EnrollmentJpaRepository : JpaRepository<EnrollmentEntity, Long> {
    fun findAllByStudentId(studentId: Long): List<EnrollmentEntity>
}
