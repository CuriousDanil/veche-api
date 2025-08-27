package com.veche.api.controller

import com.veche.api.dto.party.PartyRequestDto
import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.PartyService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * TODO()
 *
 * @property partyService TODO()
 */
@RestController
@RequestMapping("/api/parties")
class PartyController(
    private val partyService: PartyService,
) {
    @GetMapping("/all")
    fun getAllPartiesForUserCompany(
        @AuthenticationPrincipal user: UserPrincipal,
    ): List<PartyResponseDto> = partyService.getAllPartiesForUserCompany(user)

    @GetMapping
    fun getPartiesForUser(
        @AuthenticationPrincipal user: UserPrincipal,
    ): List<PartyResponseDto> = partyService.getPartiesForUser(user)

    @PostMapping
    fun createParty(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: PartyRequestDto,
    ): PartyResponseDto = partyService.createParty(request, user)

    @GetMapping("/{partyId}")
    fun getParty(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable("partyId") partyId: UUID,
    ): PartyResponseDto = partyService.getPartyById(partyId, user)
}
