package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.Student

interface StudentRepository {
    fun findOneById(id: Long): Student?

    fun save(student: Student): Student

    fun deleteAll()
}