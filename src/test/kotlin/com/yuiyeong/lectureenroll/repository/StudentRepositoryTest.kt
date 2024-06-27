package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.Student
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class StudentRepositoryTest(
    @Autowired private val studentRepository: StudentRepository
) {
    /**
     * student 를 저장했다면, 저장된 student 의 정보 가져올 수 있어야 한다.
     */
    @Test
    fun `should return saved student after saving it`() {
        // given
        val student = Student()
        val savedOne = studentRepository.save(student)

        // when
        val foundOne = studentRepository.findOneById(savedOne.id)

        // then
        Assertions.assertThat(foundOne).isNotNull
        Assertions.assertThat(foundOne!!.id).isEqualTo(savedOne.id)
    }
}