package com.yuiyeong.lectureenroll

import com.yuiyeong.lectureenroll.domain.DateTimeRange
import com.yuiyeong.lectureenroll.domain.Lecture
import com.yuiyeong.lectureenroll.domain.LectureSession
import java.time.LocalDateTime

object Helper {
    fun makeDateTimeRangeAs5Days(from: LocalDateTime) = DateTimeRange(from, from.plusDays(5))

    fun makeDateTimeRangeAs1Hour(from: LocalDateTime) = DateTimeRange(from, from.plusHours(1))

    fun createLecture(
        id: Long = 1L
    ): Lecture = Lecture(
        id = id,
        name = "test lecture",
        instructorName = "test instructor"
    )

    fun createLectureSession(
        lecture: Lecture,
        periodFrom: LocalDateTime,
        scheduleFrom: LocalDateTime,
        id: Long = 1L,
        availableCapacity: Int = 10
    ): LectureSession = LectureSession(
        id = id,
        lecture = lecture,
        capacity = 10,
        availableCapacity = availableCapacity,
        enrollmentPeriod = makeDateTimeRangeAs5Days(periodFrom),
        lecturePeriod = makeDateTimeRangeAs1Hour(scheduleFrom),
    )
}