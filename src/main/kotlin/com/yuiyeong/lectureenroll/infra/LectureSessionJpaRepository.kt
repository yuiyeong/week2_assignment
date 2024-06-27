package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.LectureSessionEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface LectureSessionJpaRepository : JpaRepository<LectureSessionEntity, Long> {
    @EntityGraph(attributePaths = ["lecture"])
    override fun findAll(): MutableList<LectureSessionEntity>
}
