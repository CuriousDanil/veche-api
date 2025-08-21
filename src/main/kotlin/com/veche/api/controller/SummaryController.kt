package com.veche.api.controller

import com.veche.api.dto.summary.SummaryResponseDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.SummaryService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class SummaryController(
    private val summaryService: SummaryService,
) {
    @GetMapping("/discussions/{discussionId}/summary")
    fun getSummaryForDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable discussionId: UUID,
    ): SummaryResponseDto = summaryService.getSummaryForDiscussion(user, discussionId)
}
