package com.yuiyeong.lectureenroll.controller

import com.yuiyeong.lectureenroll.controller.dto.LectureEnrollmentDto
import com.yuiyeong.lectureenroll.controller.dto.ListResult
import com.yuiyeong.lectureenroll.service.LectureService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("lectures")
class LectureController(
    private val lectureService: LectureService
) {
    @GetMapping("{lectureId}/application")
    fun getEnrollmentStatuses(
        @PathVariable("lectureId") lectureId: Long,
        @RequestParam("userId") studentId: Long
    ): ListResult<LectureEnrollmentDto> {
        return ListResult(
            lectureService.getLectureEnrollmentStatuses(lectureId, studentId)
                .map { LectureEnrollmentDto.from(it) }
        )
    }
}