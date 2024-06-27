package com.yuiyeong.lectureenroll

import com.yuiyeong.lectureenroll.domain.DateTimeRange
import com.yuiyeong.lectureenroll.domain.Lecture
import com.yuiyeong.lectureenroll.domain.LectureSession
import java.time.LocalDateTime

object Helper {
    fun localDateTime(): LocalDateTime = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0)

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
        capacity: Int = 10,
        availableCapacity: Int = 10
    ): LectureSession = LectureSession(
        id = id,
        lecture = lecture,
        capacity = capacity,
        availableCapacity = availableCapacity,
        enrollmentPeriod = makeDateTimeRangeAs5Days(periodFrom),
        lecturePeriod = makeDateTimeRangeAs1Hour(scheduleFrom),
    )
}