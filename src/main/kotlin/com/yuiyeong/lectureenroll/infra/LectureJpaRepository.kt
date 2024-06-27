package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.LectureEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LectureJpaRepository : JpaRepository<LectureEntity, Long>