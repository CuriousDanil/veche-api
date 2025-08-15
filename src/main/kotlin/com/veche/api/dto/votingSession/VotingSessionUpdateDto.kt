package com.veche.api.dto.votingSession

import java.time.Instant
import java.util.UUID

data class VotingSessionUpdateDto(
    val name: String?,
    val partyId: UUID?,
    val discussionIds: List<UUID>?,
    val firstRoundStart: Instant?,
    val secondRoundStart: Instant?,
    val endTime: Instant?,
)