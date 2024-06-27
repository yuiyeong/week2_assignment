package com.yuiyeong.lectureenroll.domain

import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import com.yuiyeong.lectureenroll.exception.CapacityExceededException
import com.yuiyeong.lectureenroll.exception.OutOfPeriodException
import com.yuiyeong.lectureenroll.exception.PeriodOverlapException
import java.time.LocalDateTime

class LectureSession(
    val id: Long,
    val lecture: Lecture,
    val capacity: Int,
    availableCapacity: Int,
    val enrollmentPeriod: DateTimeRange,
    val lecturePeriod: DateTimeRange
) {
    var availableCapacity = availableCapacity // 수용 가능 인원
        private set

    private var enrollments: MutableList<Enrollment> = arrayListOf() // 수강 완료 정보
}
