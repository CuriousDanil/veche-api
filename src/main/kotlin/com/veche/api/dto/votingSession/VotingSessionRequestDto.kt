package com.veche.api.dto.votingSession

import java.time.Instant
import java.util.UUID

data class VotingSessionRequestDto(
    val name: String,
    val partyId: UUID,
    val discussionIds: List<UUID>,
    val firstRoundStartsAt: Instant,
    val secondRoundStartsAt: Instant,
    val endsAt: Instant,
)
