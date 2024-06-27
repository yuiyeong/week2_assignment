package com.yuiyeong.lectureenroll.infra

import com.yuiyeong.lectureenroll.infra.entity.LectureSessionEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param

interface LectureSessionJpaRepository : JpaRepository<LectureSessionEntity, Long> {
    @EntityGraph(attributePaths = ["lecture"])
    override fun findAll(): MutableList<LectureSessionEntity>

    fun findAllByLectureId(lectureId: Long): List<LectureSessionEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    fun findOneWithLockById(@Param("id") id: Long): LectureSessionEntity?
}
