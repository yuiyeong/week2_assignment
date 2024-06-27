package com.yuiyeong.lectureenroll.controller

import com.yuiyeong.lectureenroll.controller.dto.ApplicationRequest
import com.yuiyeong.lectureenroll.controller.dto.LectureSessionDto
import com.yuiyeong.lectureenroll.controller.dto.Result
import com.yuiyeong.lectureenroll.service.LectureSessionService
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
    @PostMapping("{sessionId}/apply")
    fun enroll(
        @PathVariable("sessionId") sessionId: Long,
        @RequestBody req: ApplicationRequest
    ): Result<LectureSessionDto> {
    }}