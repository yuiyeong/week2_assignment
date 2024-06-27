package com.yuiyeong.lectureenroll.repository

import com.yuiyeong.lectureenroll.domain.Lecture
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test


@SpringBootTest
@Transactional
class LectureRepositoryTest(
    @Autowired private val lectureRepository: LectureRepository
) {
    /**
     * lecture 를 저장한 뒤, 저장한 lecture 의 정보를 반환해야한다.
     */
    @Test
    fun `should return a saved lecture after saving it`() {
        // given
        val lecture = Lecture(
            id = 0L,
            name = "test lecture",
            instructorName = "test instructor"
        )
        val savedOne = lectureRepository.save(lecture)

        // when
        val foundOne = lectureRepository.findOneById(savedOne.id)


        // then
        Assertions.assertThat(foundOne).isNotNull
        Assertions.assertThat(foundOne!!.id).isEqualTo(savedOne.id)
        Assertions.assertThat(foundOne.name).isEqualTo(savedOne.name)
        Assertions.assertThat(foundOne.instructorName).isEqualTo(savedOne.instructorName)
    }
}