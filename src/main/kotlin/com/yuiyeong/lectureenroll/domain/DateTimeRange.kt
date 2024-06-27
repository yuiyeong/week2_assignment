package com.yuiyeong.lectureenroll.domain

import com.yuiyeong.lectureenroll.exception.InvalidDateRangeException
import java.time.LocalDateTime

data class DateTimeRange(
    val start: LocalDateTime,
    val end: LocalDateTime
) {
}