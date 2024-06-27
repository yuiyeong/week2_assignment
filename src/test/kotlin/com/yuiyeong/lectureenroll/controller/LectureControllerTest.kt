package com.yuiyeong.lectureenroll.controller

import com.yuiyeong.lectureenroll.Helper.createLecture
import com.yuiyeong.lectureenroll.Helper.createLectureSession
import com.yuiyeong.lectureenroll.domain.Enrollment
import com.yuiyeong.lectureenroll.domain.Student
import com.yuiyeong.lectureenroll.repository.EnrollmentRepository
import com.yuiyeong.lectureenroll.repository.LectureRepository
import com.yuiyeong.lectureenroll.repository.LectureSessionRepository
import com.yuiyeong.lectureenroll.repository.StudentRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.Test

@AutoConfigureMockMvc
@SpringBootTest
class LectureControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val studentRepository: StudentRepository,
    private val lectureRepository: LectureRepository,
    private val lectureSessionRepository: LectureSessionRepository,
    private val enrollmentRepository: EnrollmentRepository
) {
    @AfterEach
    fun afterEach() {
        enrollmentRepository.deleteAll()
        lectureSessionRepository.deleteAll()
        lectureRepository.deleteAll()
        studentRepository.deleteAll()
    }

    /**
     * 학생이 한 특강의 모든 session 들에 대해, 각각 수강 완료 했는지 여부를 list 로 내려주어야 한다.
     */
    @Test
    fun `should return 200 ok with lecture enrollments about student`() {
        // given: 1개의 lecture 에 대한 lectureSession 이 2개 있고 그 중 1개에 수강 완료한 상황
        val student = studentRepository.save(Student())
        val periodFrom = LocalDateTime.now().minusDays(1)
        val scheduleFrom = LocalDateTime.now().plusWeeks(1)
        val lecture = lectureRepository.save(createLecture(id = 0L))
        val session1 = lectureSessionRepository.save(
            createLectureSession(lecture, periodFrom, scheduleFrom, id = 0L)
        )
        val session2 = lectureSessionRepository.save(
            createLectureSession(lecture, periodFrom, scheduleFrom.plusWeeks(1), id = 0L)
        )
        enrollmentRepository.save(Enrollment(0L, student, session1, LocalDateTime.now().minusMinutes(15)))

        // when
        val result = mockMvc.perform(
            get("/lectures/${lecture.id}/application")
                .contentType(MediaType.APPLICATION_JSON)
                .param("userId", student.id.toString())
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].lectureSessionId").value(session1.id))
            .andExpect(jsonPath("$.data[0].isEnrolled").value(true))
            .andExpect(jsonPath("$.data[0].lectureSessionId").value(session2.id))
            .andExpect(jsonPath("$.data[1].isEnrolled").value(false))
    }
}