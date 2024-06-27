package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.LectureSession

interface LectureSessionRepository {
    fun findOneById(id: Long): LectureSession?

    fun findOneWithLockById(id: Long): LectureSession?

    fun findAll(): List<LectureSession>

    fun findAllByLectureId(lectureId: Long): List<LectureSession>

    fun save(lectureSession: LectureSession): LectureSession

    fun deleteAll()
}