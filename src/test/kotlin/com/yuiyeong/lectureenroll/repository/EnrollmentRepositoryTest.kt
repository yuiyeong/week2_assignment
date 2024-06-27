package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.domain.LectureSession
import com.yuiyeong.lectureenroll.domain.Student
import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test


@SpringBootTest
@Transactional
class EnrollmentRepositoryTest @Autowired constructor(
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    private lateinit var student: Student
    private lateinit var lectureSession: LectureSession

    @BeforeEach
    fun beforeEach() {
        // given: 현재가 신청 기한 안에 있는 특강과 학생이 있는 상황
        student = studentRepository.save(Student())

        val lecture = lectureRepository.save(createLecture())

        val periodFrom = localDateTime().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
        val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
        lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))
    }

    /**
     * enrollment 를 저장한 뒤, 저장한 enrollment 를 반환해야한다.
     */
    @Test
    fun `should return a saved enrollment after save it`() {
        // when: 학생이 그 특강에 신청한 내역을 저장
        val enrollment = Enrollment(
            id = 0L,
            student = student,
            lectureSession = lectureSession,
            enrolledAt = localDateTime().minusMinutes(15)
        )
        val savedOne = enrollmentRepository.save(enrollment)

        // then
        Assertions.assertThat(savedOne.id).isNotEqualTo(enrollment.id)
        Assertions.assertThat(savedOne.student.id).isEqualTo(enrollment.student.id)
        Assertions.assertThat(savedOne.lectureSession.id).isEqualTo(enrollment.lectureSession.id)
        Assertions.assertThat(savedOne.enrolledAt).isEqualTo(enrollment.enrolledAt)
    }

    /**
     * findByStudentId 를 호출하면, 해당 student 의 enrollment 만 반환되어야한다.
     */
    @Test
    fun `should return history of student when calling findByStudentId`() {
        // given: 두 학생과 특강이 있고, 학생 각각의 신청 완료 내역이 1개씩 존재하는 상황
        val anotherStudent = studentRepository.save(Student())
        enrollmentRepository.save(
            Enrollment(0L, anotherStudent, lectureSession, localDateTime().minusMinutes(15))
        )

        // given: student 의 수강 신청 완료 내역
        val savedOne = enrollmentRepository.save(
            Enrollment(0L, student, lectureSession, localDateTime().minusMinutes(14))
        )

        // when: student 의 수강 완료 내역만 가져옴
        val history = enrollmentRepository.findAllByStudentId(student.id)

        // then: student 의 수강 완료 내역만 와야함
        Assertions.assertThat(history.count()).isEqualTo(1)
        Assertions.assertThat(history[0].id).isEqualTo(savedOne.id)
        Assertions.assertThat(history[0].student.id).isEqualTo(savedOne.student.id)
        Assertions.assertThat(history[0].lectureSession.id).isEqualTo(savedOne.lectureSession.id)
        Assertions.assertThat(history[0].enrolledAt).isEqualTo(savedOne.enrolledAt)
    }


    /**
     * 한 학생이 중복으로 강의에 수강이 되려고 하면, unique 제약 조건 exception 이 발생해야한다.
     */
    @Test
    fun `should throw constraints exception when trying to save duplicated enrollment`() {
        // given: 한 학생이 강의에 대해 수강 완료한 내역이 있는 상황
        enrollmentRepository.save(Enrollment(0L, student, lectureSession, localDateTime().minusMinutes(15)))

        // when & then: 위 강의에 중복으로 수강 완료가 될 수 없어야 한다.
        Assertions.assertThatThrownBy {
            enrollmentRepository.save(
                Enrollment(0L, student, lectureSession, localDateTime().minusMinutes(14))
            )
        }.isInstanceOf(UnexpectedRollbackException::class.java)
            .cause()
            .isInstanceOf(AlreadyEnrolledException::class.java)
    }
}