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

    init {
        if (enrollmentPeriod.overlaps(lecturePeriod))
            throw PeriodOverlapException("신청 기간과 강의 시간을 겹칠 수 없습니다.")
    }

    fun addEnrollments(enrollments: List<Enrollment>) {
        this.enrollments.addAll(enrollments)
    }

    fun enrollments(): List<Enrollment> = enrollments


    fun enroll(student: Student, at: LocalDateTime): Enrollment {
        checkEnrollmentPeriod(at)
        checkEnrollments(student)
        checkAvailableCapacity()

        availableCapacity--

        return Enrollment(
            id = 0L,
            student = student,
            lectureSession = this,
            enrolledAt = at
        ).also {
            enrollments.add(it)
        }
    }

    private fun checkEnrollmentPeriod(moment: LocalDateTime) {
        if (!enrollmentPeriod.contains(moment))
            throw OutOfPeriodException("현재는 신청 기한이 아닙니다.")
    }

    private fun checkAvailableCapacity() {
        if (availableCapacity == 0)
            throw CapacityExceededException("정원이 마감된 강의입니다.")
    }

    private fun checkEnrollments(student: Student) {
        val alreadyEnrolled = enrollments.any { it.student == student }
        if (alreadyEnrolled)
            throw AlreadyEnrolledException("이미 신청 완료한 강의입니다.")
    }
}
