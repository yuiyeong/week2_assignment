package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.Lecture

interface LectureRepository {
    fun findOneById(id: Long): Lecture?

    fun save(lecture: Lecture): Lecture

    fun deleteAll()
}