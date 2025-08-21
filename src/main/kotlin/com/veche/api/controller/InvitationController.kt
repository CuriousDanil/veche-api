package com.veche.api.controller

import com.veche.api.dto.invitation.InvitationCreateResponseDto
import com.veche.api.dto.invitation.InvitationRequestDto
import com.veche.api.dto.invitation.InvitationResponseDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.InvitationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/invitations")
class InvitationController(
    private val invitationService: InvitationService,
) {
    @PostMapping
    fun createInvitation(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody createDto: InvitationRequestDto,
    ): InvitationCreateResponseDto = invitationService.createInvitation(user, createDto)
}
