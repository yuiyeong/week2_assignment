package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.domain.Lecture
import com.yuiyeong.lectureenroll.domain.LectureSession
import com.yuiyeong.lectureenroll.domain.Student
import com.yuiyeong.lectureenroll.exception.AlreadyEnrolledException
import com.yuiyeong.lectureenroll.exception.CapacityExceededException
import com.yuiyeong.lectureenroll.exception.LectureSessionNotFoundException
import com.yuiyeong.lectureenroll.exception.OutOfPeriodException
import com.yuiyeong.lectureenroll.exception.StudentNotFoundException
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

@SpringBootTest
class LectureSessionServiceTest @Autowired constructor(
    private val lectureSessionService: LectureSessionService,
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {

    @Nested
    @Transactional
    inner class EnrollmentTest {

        /**
         * 특강 신청 기간에 정원 안에 들면서 신청이 되었다면,
         * 신청 내역이 저장되고, 신청된 강좌 세션의 정보를 반환해야한다.
         */
        @Test
        fun `should return a lecture session when enrolling the lecture session`() {
            // given: 현재가 신청 기한에 포함되었고, 정원이 남아있는 lecture session
            val student = studentRepository.save(Student())
            val lecture = lectureRepository.save(createLecture())

            val periodFrom = localDateTime().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when
            val result = lectureSessionService.enroll(lectureSession.id, student.id)

            // then: 수강된 lecture session 에 대한 정보가 오고,
            Assertions.assertThat(result.id).isEqualTo(lectureSession.id)
            Assertions.assertThat(result.lecture.id).isEqualTo(lecture.id)
            Assertions.assertThat(result.lecture.name).isEqualTo(lecture.name)
            Assertions.assertThat(result.lecture.instructorName).isEqualTo(lecture.instructorName)
            Assertions.assertThat(result.capacity).isEqualTo(lectureSession.capacity)
            Assertions.assertThat(result.availableCapacity).isEqualTo(lectureSession.availableCapacity - 1)
            Assertions.assertThat(result.enrollmentPeriod).isEqualTo(lectureSession.enrollmentPeriod)
            Assertions.assertThat(result.lecturePeriod).isEqualTo(lectureSession.lecturePeriod)

            // 신청 내역에 성공 내역이 늘었는지 확인
            val history = enrollmentRepository.findAllByStudentId(student.id)
            Assertions.assertThat(history.count()).isEqualTo(1)
            Assertions.assertThat(history.last().student.id).isEqualTo(student.id)
            Assertions.assertThat(history.last().lectureSession.lecture.id).isEqualTo(lecture.id)
            Assertions.assertThat(history.last().lectureSession.id).isEqualTo(lectureSession.id)
        }

        /**
         * 알 수 없는 studentId 에 대해서는 StudentNotFoundException 을 던저야 한다.
         */
        @Test
        fun `should throw StudentNotFoundException when unknown studentId`() {
            // given
            val unknownStudentId = 2L
            val lecture = lectureRepository.save(createLecture())
            val periodFrom = localDateTime().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(lectureSession.id, unknownStudentId) }
                .isInstanceOf(StudentNotFoundException::class.java)
                .hasMessageContaining("없습니다.")
        }

        /**
         * 알 수 없는 lectureSessionId 에 대해서는 LectureSessionNotFoundException 을 던저야 한다.
         */
        @Test
        fun `should throw LectureSessionNotFoundException when unknown lectureId`() {
            // given
            val student = studentRepository.save(Student())

            val unknownLectureSessionId = 2L

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(unknownLectureSessionId, student.id) }
                .isInstanceOf(LectureSessionNotFoundException::class.java)
                .hasMessageContaining("없습니다.")
        }

        /**
         * 특강 신청 기한의 시작 전에 학생이 신청하면,
         * OutOfPeriodException 을 발생시킨다.
         */
        @Test
        fun `should throw OutOfPeriodException when trying to enroll before period starts`() {
            // given: 신청 기한이 현재 이후인 lecture session
            val student = studentRepository.save(Student())
            val lecture = lectureRepository.save(createLecture())
            val periodFrom = localDateTime().plusHours(1) // 1 시간 뒤부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(lectureSession.id, student.id) }
                .isInstanceOf(OutOfPeriodException::class.java)
                .hasMessageContaining("현재는 신청 기한이 아닙니다.")
        }

        /**
         * 특강 신청 기한의 마감 후에 학생이 신청하면,
         * OutOfPeriodException 을 발생시킨다.
         */
        @Test
        fun `should throw OutOfPeriodException when trying to enroll after period ends`() {
            // given: 학생과 신청 기한이 지난 lecture session
            val student = studentRepository.save(Student())
            val lecture = lectureRepository.save(createLecture())
            val periodFrom = localDateTime().minusMonths(1) // 1 달 전부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusDays(1) // 하루 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(lectureSession.id, student.id) }
                .isInstanceOf(OutOfPeriodException::class.java)
                .hasMessageContaining("현재는 신청 기한이 아닙니다.")
        }

        /**
         * 정원이 다 찬 특강에 학생이 신청하면,
         * CapacityExceededException 을 발생시킨다.
         */
        @Test
        fun `should throw CapacityExceededException when trying to enroll in a fully booked lecture`() {
            // given: 현재가 신청 기한에 포함되어 있지만, 수강 가능 인원이 없는 lecture session
            val student = studentRepository.save(Student())
            val lecture = lectureRepository.save(createLecture())
            val periodFrom = localDateTime().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(
                createLectureSession(lecture, periodFrom, scheduleFrom, availableCapacity = 0)
            )

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(lectureSession.id, student.id) }
                .isInstanceOf(CapacityExceededException::class.java)
                .hasMessageContaining("정원이 마감된 강의입니다.")
        }


        /**
         * 이미 수강이 된 강의에 학생이 중복 신청하면,
         * AlreadyEnrolledException 을 발생시킨다.
         */
        @Test
        fun `should throw AlreadyEnrolledException when trying to enroll in the already booked lecture`() {
            // given: 현재가 신청 기한에 포함되었고, 정원이 남아있는 lecture 와
            val student = studentRepository.save(Student())
            val lecture = lectureRepository.save(createLecture())
            val periodFrom = localDateTime().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = localDateTime().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // given: 해당 강의에 수강이 된 사용자
            enrollmentRepository.save(
                Enrollment(1L, student, lectureSession, localDateTime().minusMinutes(5))
            )

            // when & then
            Assertions.assertThatThrownBy { lectureSessionService.enroll(lectureSession.id, student.id) }
                .isInstanceOf(AlreadyEnrolledException::class.java)
                .hasMessageContaining("이미 신청 완료한 강의입니다.")
        }
    }
}