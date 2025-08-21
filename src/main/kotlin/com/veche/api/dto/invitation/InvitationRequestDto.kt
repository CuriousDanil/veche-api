package com.veche.api.dto.invitation

import java.util.UUID

data class InvitationRequestDto(
    val name: String,
    val bio: String?,
    val email: String?,
    val partyId: UUID,
)
