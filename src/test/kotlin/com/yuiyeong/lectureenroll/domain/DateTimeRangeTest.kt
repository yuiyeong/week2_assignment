package com.yuiyeong.lectureenroll.domain

import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.Helper.makeDateTimeRangeAs5Days
import com.yuiyeong.lectureenroll.exception.InvalidDateRangeException
import org.assertj.core.api.Assertions
import kotlin.test.Test


class DateTimeRangeTest {

    /**
     * DatetimeRange instance 를 만드는데, start 가 end 보다 나중이면
     * InvalidDateRangeException 을 발생시켜야 한다.
     */
    @Test
    fun `should throw InvalidDateRangeException when start is after end`() {
        // given
        val start = localDateTime()
        val end = start.minusDays(1)
        // when & then
        Assertions.assertThatThrownBy { DateTimeRange(start, end) }
            .isInstanceOf(InvalidDateRangeException::class.java)
            .hasMessageContaining("시작 시점은 끝 시점보다 앞서야합니다.")
    }

    /**
     * 시점이 시작과 끝 사이에 있다면, true 를 반환하고
     * 시작과 끝 밖에 있다면, false 를 반환해야합니다.
     */
    @Test
    fun `should return if moment is between start and end`() {
        // given
        val period = makeDateTimeRangeAs5Days(localDateTime().minusDays(1))
        val inMoment = localDateTime()
        val outMoment = localDateTime().minusMonths(2)

        // when & then
        Assertions.assertThat(period.contains(inMoment)).isEqualTo(true)
        Assertions.assertThat(period.contains(outMoment)).isEqualTo(false)
    }

    /**
     * 같은 기간일 때,
     * 한 기간의 끝 시점이 다른 기간의 시작 시점보다 미래일 때,
     * 한 기간의 끝 시점과 다른 기간의 시작 시점이 같을 때,
     * 한 기간의 시작 시점과 다른 기간의 끝 시점이 같을 때,
     * overlaps 는 true 를 반환해야한다.
     */
    @Test
    fun `should return true when two DateTimeRanges overlap`() {
        // given
        val period1 = makeDateTimeRangeAs5Days(localDateTime().minusDays(1))
        // 같은 기간일 때,
        val period2 = DateTimeRange(period1.start, period1.end)
        // 한 기간의 끝 시점이 다른 기간의 시작 시점보다 미래일 때,
        val period3 = DateTimeRange(period1.end.minusDays(1), period1.end.plusDays(1))
        // 한 기간의 끝 시점과 다른 기간의 시작 시점이 같을 때,
        val period4 = DateTimeRange(period1.end, period1.end.plusHours(1))
        // 한 기간의 시작 시점과 다른 기간의 끝 시점이 같을 때,
        val period5 = DateTimeRange(period1.start.minusDays(1), period1.start)

        // when & then
        /*
        period1: |--------------------------|
        period2: |--------------------------|
        */
        Assertions.assertThat(period1.overlaps(period2)).isEqualTo(true)

        /*
        period1: |--------------------------|
        period3:               |--------------------------|
        */
        Assertions.assertThat(period1.overlaps(period3)).isEqualTo(true)

        /*
        period1: |--------------------------|
        period4:                            |--------------------------|
        */
        Assertions.assertThat(period1.overlaps(period4)).isEqualTo(true)

        /*
        period1:                           |--------------------------|
        period5: |-------------------------|
        */
        Assertions.assertThat(period1.overlaps(period5)).isEqualTo(true)
    }

    /**
     * 한 기간의 끝 시점보다 다른 기간의 시작 시점이 미래일 때,
     * 한 기간의 시작 시점보다 다른 기간의 끝 시점이 과거일 때,
     * overlaps 는 false 를 반환한다.
     */
    @Test
    fun `should return false when two DatetimeRanges do not overlap`() {
        // given
        val period1 = makeDateTimeRangeAs5Days(localDateTime().minusDays(1))
        // 한 기간의 끝 시점보다 다른 기간의 시작 시점이 미래일 때,
        val period2 = DateTimeRange(period1.end.plusSeconds(1), period1.end.plusSeconds(2))
        // 한 기간의 시작 시점보다 다른 기간의 끝 시점이 과거일 때,
        val period3 = DateTimeRange(period1.start.minusSeconds(2), period1.start.minusSeconds(1))

        // when & then
        /*
        period1: |-----------------|
        period2:                    |--|
        */
        Assertions.assertThat(period1.overlaps(period2)).isEqualTo(false)

        /*
        period1:     |-----------------|
        period3: |--|
        */
        Assertions.assertThat(period1.overlaps(period3)).isEqualTo(false)
    }
}