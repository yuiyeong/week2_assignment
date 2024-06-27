package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.Enrollment

interface EnrollmentRepository {
    fun findAllByStudentId(studentId: Long): List<Enrollment>

    fun findAllByLectureIdAndStudentId(lectureId: Long, studentId: Long): List<Enrollment>

    fun save(enrollment: Enrollment): Enrollment

    fun deleteAll()
}