package com.veche.api.dto.invitation

import java.util.UUID

data class InvitationResponseDto(
    val partyId: UUID,
    val companyName: String,
    val suggestedName: String?,
    val suggestedBio: String?,
    val suggestedEmail: String?,
    val expiresAt: java.time.Instant,
)
