package com.yuiyeong.lectureenroll.domain

import com.yuiyeong.lectureenroll.Helper
import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.Helper.makeDateTimeRangeAs1Hour
import com.yuiyeong.lectureenroll.Helper.makeDateTimeRangeAs5Days
import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import com.yuiyeong.lectureenroll.exception.CapacityExceededException
import com.yuiyeong.lectureenroll.exception.OutOfPeriodException
import com.yuiyeong.lectureenroll.exception.PeriodOverlapException
import org.assertj.core.api.Assertions
import java.time.LocalDateTime
import kotlin.test.Test

class LectureSessionTest {
    private val student: Student = Student(21L)

    /**
     * 생성 중 신청 기간과 강의 시간이 겹치면,
     * PeriodOverlapException 를 발생시키다.
     */
    @Test
    fun `should throw EnrollmentPeriodOverlapException when initiating lecture`() {
        // when & then
        Assertions.assertThatThrownBy {
            val lecture = createLecture()
            val periodFrom = localDateTime().plusHours(1)
            val scheduleFrom = periodFrom.minusSeconds(2)
            Helper.createLectureSession(lecture, periodFrom, scheduleFrom)
        }.isInstanceOf(PeriodOverlapException::class.java)
            .hasMessageContaining("신청 기간과 강의 시간을 겹칠 수 없습니다.")
    }

    /**
     * 신청 기간 중 정원 안에 들어 신청이 되었다면,
     * 수강 가능 인원이 한 명 줄고, 수강 완료 내역이 1개 증가해야한다.
     */
    @Test
    fun `should return a lecture when enrolling the lecture`() {
        // given
        val lecture = createLecture()
        val lectureSession = createLectureSession(lecture)
        val enrollmentCount = lectureSession.enrollments().count()
        val availableCapacity = lectureSession.availableCapacity
        val at = localDateTime()

        // when
        lectureSession.enroll(student, at)

        // then: availableCapacity 1개 줄고,
        Assertions.assertThat(lectureSession.availableCapacity).isEqualTo(availableCapacity - 1)
        // enrollments 가 1개 증가해야한다.
        Assertions.assertThat(lectureSession.enrollments().count()).isEqualTo(enrollmentCount + 1)
        Assertions.assertThat(lectureSession.enrollments()[0].student).isEqualTo(student)
        Assertions.assertThat(lectureSession.enrollments()[0].enrolledAt).isEqualTo(at)
    }

    /**
     * 신청 기한의 시작 전에 학생이 신청하면,
     * OutOfPeriodException 을 발생시킨다.
     */
    @Test
    fun `should throw OutOfPeriodException when trying to enroll before period starts`() {
        // given: 강의, 학생, 신청 시작보다 1분 전인 시점
        val lecture = createLecture()
        val lectureSession = createLectureSession(lecture)
        val at = lectureSession.enrollmentPeriod.start.minusMinutes(1)

        // when & then
        Assertions.assertThatThrownBy { lectureSession.enroll(student, at) }
            .isInstanceOf(OutOfPeriodException::class.java)
            .hasMessageContaining("현재는 신청 기한이 아닙니다.")
    }

    /**
     * 신청 기한의 마감 후에 학생이 신청하면,
     * OutOfPeriodException 을 발생시킨다.
     */
    @Test
    fun `should throw OutOfPeriodException when trying to enroll after period ends`() {
        // given: 강의, 학생, 신청 마감보다 1분 후인 시점
        val lecture = createLecture()
        val lectureSession = createLectureSession(lecture)
        val at = lectureSession.enrollmentPeriod.end.plusMinutes(1)

        // when & then
        Assertions.assertThatThrownBy { lectureSession.enroll(student, at) }
            .isInstanceOf(OutOfPeriodException::class.java)
            .hasMessageContaining("현재는 신청 기한이 아닙니다.")
    }

    /**
     * 정원이 다 찬 특강에 학생이 신청하면,
     * CapacityExceededException 을 발생시킨다.
     */
    @Test
    fun `should throw CapacityExceededException when trying to enroll in a fully booked lecture`() {
        // given: 정원이 마감된 강의, 학생, 신청 기간 안에 있는 시점(현재)
        val lecture = createLecture()
        val lectureSession = createLectureSession(lecture, availableCapacity = 0)
        val at = localDateTime()

        // when & then
        Assertions.assertThatThrownBy { lectureSession.enroll(student, at) }
            .isInstanceOf(CapacityExceededException::class.java)
            .hasMessageContaining("정원이 마감된 강의입니다.")
    }

    /**
     * 이미 수강이 된 강의에 학생이 중복 신청하면,
     * AlreadyEnrolledException 을 발생시킨다.
     */
    @Test
    fun `should throw AlreadyEnrolledException when trying to enroll in the already booked lecture`() {
        // given: 학생, 그 학생이 신청 완료한 강의, 신청 기간 안에 있는 시점(현재)
        val lecture = createLecture()
        val lectureSession = createLectureSession(lecture)
        val at = localDateTime()
        lectureSession.enroll(student, at) // 신청 완료 내역 추가

        // when & then
        Assertions.assertThatThrownBy { lectureSession.enroll(student, at) }
            .isInstanceOf(AlreadyEnrolledException::class.java)
            .hasMessageContaining("이미 신청 완료한 강의입니다.")
    }

    private fun createLectureSession(
        lecture: Lecture,
        periodFrom: LocalDateTime = localDateTime().minusDays(1), // 1 일 전부터 5일 동안 신청 기한
        scheduleFrom: LocalDateTime = localDateTime().plusWeeks(1), // 1 주일 뒤부터 1시간 동안 강의 시간
        availableCapacity: Int = 10
    ): LectureSession {
        return LectureSession(
            id = 1L,
            lecture = lecture,
            capacity = 10,
            availableCapacity = availableCapacity,
            enrollmentPeriod = makeDateTimeRangeAs5Days(periodFrom),
            lecturePeriod = makeDateTimeRangeAs1Hour(scheduleFrom),
        )
    }
}