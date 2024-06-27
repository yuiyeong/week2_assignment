package com.yuiyeong.lectureenroll.controller

import com.yuiyeong.lectureenroll.controller.dto.ApplicationRequest
import com.yuiyeong.lectureenroll.controller.dto.LectureSessionDto
import com.yuiyeong.lectureenroll.controller.dto.ListResult
import com.yuiyeong.lectureenroll.controller.dto.Result
import com.yuiyeong.lectureenroll.service.LectureSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lecture-sessions")
class LectureSessionController(
    private val lectureSessionService: LectureSessionService
) {

    @GetMapping
    fun list(): ListResult<LectureSessionDto> {
        return ListResult(
            lectureSessionService.findAll().map { LectureSessionDto.from(it) }
        )
    }

    @PostMapping("{sessionId}/apply")
    fun enroll(
        @PathVariable("sessionId") sessionId: Long,
        @RequestBody req: ApplicationRequest
    ): Result<LectureSessionDto> {
        return Result(
            LectureSessionDto.from(lectureSessionService.enroll(sessionId, req.studentId))
        )
    }
}