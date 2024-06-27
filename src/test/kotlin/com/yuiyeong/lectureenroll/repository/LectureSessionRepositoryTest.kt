package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.domain.Lecture
import com.yuiyeong.lectureenroll.domain.Student
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test


@SpringBootTest
@Transactional
class LectureSessionRepositoryTest @Autowired constructor(
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val lectureSessionRepository: LectureSessionRepository
) {
    private lateinit var student: Student
    private lateinit var lecture: Lecture

    @BeforeEach
    fun beforeEach() {
        // given: 현재가 신청 기한 안에 있는 특강과 학생이 있는 상황
        student = studentRepository.save(Student())
        lecture = lectureRepository.save(createLecture())
    }

    /**
     * lectureSession 을 저장한 뒤, 저장한 lectureSession 의 정보를 반환해야한다.
     */
    @Test
    fun `should return a saved lecture session after saving it`() {
        // given
        val periodFrom = localDateTime().minusDays(1)
        val scheduleFrom = localDateTime().plusWeeks(1)
        val lectureSession = createLectureSession(lecture, periodFrom, scheduleFrom)
        val savedOne = lectureSessionRepository.save(lectureSession)

        // when
        val foundOne = lectureSessionRepository.findOneById(savedOne.id)


        // then
        Assertions.assertThat(foundOne).isNotNull
        Assertions.assertThat(foundOne!!.id).isEqualTo(savedOne.id)
        Assertions.assertThat(foundOne.lecture.id).isEqualTo(savedOne.lecture.id)
        Assertions.assertThat(foundOne.capacity).isEqualTo(savedOne.capacity)
        Assertions.assertThat(foundOne.availableCapacity).isEqualTo(savedOne.availableCapacity)
        Assertions.assertThat(foundOne.enrollmentPeriod).isEqualTo(savedOne.enrollmentPeriod)
        Assertions.assertThat(foundOne.lecturePeriod).isEqualTo(savedOne.lecturePeriod)
    }

    /**
     * 모든 lectureSession 을 lecture 정보와 함께 가져와야 한다.
     */
    @Test
    fun `should return all lecture sessions with the lecture`() {
        // given
        val sessionSize = 5
        (0..<sessionSize).forEach {
            val delta = it.toLong()
            val lectureSession = createLectureSession(
                lecture,
                localDateTime().minusDays(1 + delta),
                localDateTime().plusWeeks(1 + delta),
                id = 0L
            )
            lectureSessionRepository.save(lectureSession)
        }

        // when
        val lectureSessions = lectureSessionRepository.findAll()

        // then
        Assertions.assertThat(lectureSessions.count()).isEqualTo(sessionSize)
        lectureSessions.forEach {
            Assertions.assertThat(it.lecture.id).isEqualTo(lecture.id)
        }
    }
}