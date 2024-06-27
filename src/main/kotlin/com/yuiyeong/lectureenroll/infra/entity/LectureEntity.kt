package com.yuiyeong.lectureenroll.infra.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "lecture")
class LectureEntity(
    id: Long,
    name: String,
    instructorName: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = id
        private set

    var name: String = name
        private set

    var instructorName: String = instructorName
        private set
}