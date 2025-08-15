package com.veche.api.controller

import com.veche.api.dto.votingsession.VotingSessionRequestDto
import com.veche.api.dto.votingsession.VotingSessionResponseDto
import com.veche.api.dto.votingsession.VotingSessionUpdateDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.VotingSessionService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/voting-sessions")
class VotingSessionController(
    private val votingSessionService: VotingSessionService,
) {
    @GetMapping
    fun getAllVotingSessions(
        @AuthenticationPrincipal user: UserPrincipal,
    ): List<VotingSessionResponseDto> = votingSessionService.getAllVotingSessions(user)

    @GetMapping("/{id}")
    fun getVotingSessionById(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
    ): VotingSessionResponseDto = votingSessionService.getVotingSessionById(user, id)

    @PostMapping
    fun createVotingSession(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: VotingSessionRequestDto,
    ): VotingSessionResponseDto = votingSessionService.createVotingSession(user, request)

    @PatchMapping("/{id}")
    fun updateVotingSession(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable id: UUID,
        @RequestBody updateDto: VotingSessionUpdateDto,
    ): ResponseEntity<VotingSessionResponseDto> {
        val updated = votingSessionService.updateVotingSession(user, id, updateDto)
        return ResponseEntity.ok(updated)
    }
}
