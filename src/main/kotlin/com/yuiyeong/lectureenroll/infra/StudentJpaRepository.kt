package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.StudentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StudentJpaRepository : JpaRepository<StudentEntity, Long>