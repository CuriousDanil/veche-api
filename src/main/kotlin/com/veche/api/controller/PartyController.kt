package com.veche.api.controller

import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.PartyService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/parties")
class PartyController(
    private val partyService: PartyService,
) {
    @GetMapping
    fun getAllPartiesForUserCompany(
        @AuthenticationPrincipal user: UserPrincipal,
    ): List<PartyResponseDto> =
        partyService.getAllPartiesForUserCompany(user)
}
