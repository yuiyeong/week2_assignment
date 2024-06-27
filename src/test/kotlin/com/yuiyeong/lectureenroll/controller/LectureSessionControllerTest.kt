package com.yuiyeong.lectureenroll.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.controller.dto.LectureSessionDto
import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.domain.Lecture
import com.yuiyeong.lectureenroll.domain.Student
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.Test


@AutoConfigureMockMvc
@SpringBootTest
class LectureSessionControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    @Nested
    inner class ApplicationTest {
        private lateinit var student: Student
        private lateinit var lecture: Lecture

        @BeforeEach
        fun beforeEach() {
            student = studentRepository.save(Student())
            lecture = lectureRepository.save(createLecture())
        }

        /**
         * 신청 시 특강 신청 기간에 정원 안에 들었다면, 신청이 되고 강좌 세션 정보를 내려주어야 한다.
         */
        @Test
        fun `should return 200 ok with an enrolled lecture session`() {
            // given
            val periodFrom = LocalDateTime.now().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = LocalDateTime.now().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when
            val result = mockMvc.perform(
                post("/lecture-sessions/${lectureSession.id}/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(RequestApply(student.id)))
            )

            // then
            result.andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(lectureSession.id))
                .andExpect(jsonPath("$.data.lectureName").value(lecture.name))
                .andExpect(jsonPath("$.data.lectureInstructorName").value(lecture.instructorName))
                .andExpect(
                    jsonPath("$.data.sessionStart").value(
                        lectureSession.lecturePeriod.start.format(LectureSessionDto.formatter)
                    )
                )
                .andExpect(
                    jsonPath("$.data.sessionEnd").value(
                        lectureSession.lecturePeriod.end.format(LectureSessionDto.formatter)
                    )
                )
        }


        /**
         * 특강 신청 기한의 시작 전에 학생이 신청하면,
         * 기한이 맞지 않아 실패했다는 error response 를 내려줘야한다.
         */
        @Test
        fun `should return 400 with error response when trying to enroll before period starts`() {
            // given
            val periodFrom = LocalDateTime.now().plusHours(1) // 1 시간 뒤부터 5일 동안 신청 기한
            val scheduleFrom = LocalDateTime.now().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when
            val result = mockMvc.perform(
                post("/lecture-sessions/${lectureSession.id}/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(RequestApply(student.id)))
            )

            // then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value("현재는 신청 기한이 아닙니다."))
        }

        /**
         * 특강 신청 기한의 마감 후에 학생이 신청하면,
         * 기한이 맞지 않아 실패했다는 error response 를 내려줘야한다.
         */
        @Test
        fun `should return 400 with error response when trying to enroll after period ends`() {
            // given
            val periodFrom = LocalDateTime.now().minusMonths(1) // 1 달 전부터 5일 동안 신청 기한
            val scheduleFrom = LocalDateTime.now().plusDays(1) // 하루 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))

            // when
            val result = mockMvc.perform(
                post("/lecture-sessions/${lectureSession.id}/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(RequestApply(student.id)))
            )

            // then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value("현재는 신청 기한이 아닙니다."))
        }


        /**
         * 정원이 다 찬 특강에 학생이 신청하면,
         * 정원이 초과했다는 error response 를 내려줘야한다.
         */
        @Test
        fun `should return 400 with error response when trying to enroll in a fully booked lecture`() {
            // given: 현재가 신청 기한에 포함되어 있지만, 수강 가능 인원이 없는 lecture
            val periodFrom = LocalDateTime.now().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = LocalDateTime.now().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(
                createLectureSession(lecture, periodFrom, scheduleFrom, availableCapacity = 0)
            )

            // when
            val result = mockMvc.perform(
                post("/lecture-sessions/${lectureSession.id}/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(RequestApply(student.id)))
            )

            // then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value("정원이 마감된 강의입니다."))
        }


        /**
         * 이미 수강이 된 강의에 학생이 중복 신청하면,
         * 중복 신청했다는 error response 를 내려줘야한다.
         */
        @Test
        fun `should return 400 bad request when trying to enroll in the already booked lecture`() {
            // given: 현재가 신청 기한에 포함되었고, 정원이 남아있는 lecture 와 해당 강의에 수강이 된 사용자
            val periodFrom = LocalDateTime.now().minusDays(1) // 1 일 전부터 5일 동안 신청 기한
            val scheduleFrom = LocalDateTime.now().plusWeeks(1) // 1 주일 뒤부터 1시간 동안 강의 시간
            val lectureSession = lectureSessionRepository.save(createLectureSession(lecture, periodFrom, scheduleFrom))
            enrollmentRepository.save(Enrollment(1L, student, lectureSession, LocalDateTime.now().minusMinutes(5)))

            // when
            val result = mockMvc.perform(
                post("/lecture-sessions/${lectureSession.id}/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(RequestApply(student.id)))
            )

            // then
            result.andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value("이미 신청 완료한 강의입니다."))
        }
    }

    data class RequestApply(val studentId: Long)
}