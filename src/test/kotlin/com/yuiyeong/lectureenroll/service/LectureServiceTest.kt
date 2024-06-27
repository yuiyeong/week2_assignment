package com.yuiyeong.lectureenroll.service

import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.Helper.localDateTime
import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.domain.Student
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class LectureServiceTest @Autowired constructor(
    private val lectureService: LectureService,
    private val lectureRepository: LectureRepository,
    private val studentRepository: StudentRepository,
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {

    /**
     * lecture 의 모든 session 에 대해서 student 의 수강 완료 여부를 SessionEnrollmentStatus 리스트로 반환해야한다.
     */
    @Test
    fun `should return lectureSessions with enrollments`() {
        // given: 한 lecture 에 5 개의 session 을 만들고, 그 중 3개에 수강 신청 완료한 상황
        val student = studentRepository.save(Student())
        val lecture = lectureRepository.save(createLecture())
        val sessionSize = 5
        (0..<sessionSize).forEach {
            val delta = it.toLong()
            val lectureSession = createLectureSession(
                lecture,
                localDateTime().minusDays(1 + delta),
                localDateTime().plusWeeks(1 + delta),
                id = 0L
            )
            val session = lectureSessionRepository.save(lectureSession)
            if (it % 2 == 0) {
                enrollmentRepository.save(
                    Enrollment(0L, student, session, localDateTime().minusMinutes(15 - it.toLong()))
                )
            }
        }

        // when
        val sessionEnrollmentStatuses = lectureService.getLectureEnrollmentStatuses(lecture.id, student.id)

        // then
        Assertions.assertThat(sessionEnrollmentStatuses.count()).isEqualTo(sessionSize)
        Assertions.assertThat(sessionEnrollmentStatuses.count { it.isEnrolled }).isEqualTo(3)
    }
}