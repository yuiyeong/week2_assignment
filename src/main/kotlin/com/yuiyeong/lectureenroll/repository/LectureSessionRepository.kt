package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.LectureSession

interface LectureSessionRepository {

    fun findOneById(id: Long): LectureSession?

    fun save(lectureSession: LectureSession): LectureSession

    fun deleteAll()
}