package com.yuiyeong.lectureenroll.infra.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime


@Entity
@Table(
    name = "enrollment",
    uniqueConstraints = [UniqueConstraint(columnNames = ["lecture_session_id", "student_id"])]
)
class EnrollmentEntity(
    id: Long,
    studentEntity: StudentEntity,
    lectureSessionEntity: LectureSessionEntity,
    enrolledAt: LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = id
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    var student: StudentEntity = studentEntity
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_session_id")
    var lectureSession: LectureSessionEntity = lectureSessionEntity
        private set

    var enrolledAt: LocalDateTime = enrolledAt
        private set
}
