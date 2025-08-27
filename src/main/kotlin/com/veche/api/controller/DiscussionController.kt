package com.veche.api.controller

import com.veche.api.database.model.VoteValue
import com.veche.api.dto.discussion.DiscussionRequestDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import com.veche.api.dto.discussion.DiscussionUpdateDto
import com.veche.api.event.ActionPayload
import com.veche.api.security.UserPrincipal
import com.veche.api.service.DiscussionService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * TODO()
 *
 * @property discussionService TODO()
 */
@RestController
@RequestMapping("/api/discussions")
class DiscussionController(
    private val discussionService: DiscussionService,
) {
    @GetMapping
    fun getAllDiscussions(
        @AuthenticationPrincipal user: UserPrincipal,
    ): List<DiscussionResponseDto> = discussionService.getAllDiscussionsForUserCompany(user)

    @PostMapping
    fun createDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: DiscussionRequestDto,
    ): DiscussionResponseDto = discussionService.createDiscussion(user, request)

    @PostMapping("/{discussionId}/vote")
    fun voteOnDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable discussionId: UUID,
        @RequestParam vote: VoteValue,
    ) {
        discussionService.voteOnDiscussion(user, discussionId, vote)
    }

    @PatchMapping("/{id}")
    fun updateDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
        @RequestBody updateDto: DiscussionUpdateDto,
    ): ResponseEntity<DiscussionResponseDto> {
        val updated = discussionService.updateDiscussion(user, id, updateDto)
        return ResponseEntity.ok(updated)
    }

    @PostMapping("/{id}/archive")
    fun archiveDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        discussionService.archiveDiscussion(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/wait")
    fun putDiscussionOnWait(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        discussionService.putDiscussionOnWait(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/resolve")
    fun resolveDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        discussionService.resolveDiscussion(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/voting")
    fun putDiscussionOnVoting(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        discussionService.putDiscussionOnVoting(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/final-voting")
    fun putDiscussionOnFinalVoting(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        discussionService.putDiscussionOnFinalVoting(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/action")
    fun addActionToDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
        @RequestBody action: ActionPayload,
    ): ResponseEntity<Void> {
        discussionService.addActionToDiscussion(user, id, action)
        return ResponseEntity.noContent().build()
    }
}
