package com.veche.api.dto.company

import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.dto.user.UserResponseDto
import java.util.UUID

data class CompanyResponseDto(
    val id: UUID,
    val name: String,
    val users: List<UserResponseDto>,
    val parties: List<PartyResponseDto>,
)