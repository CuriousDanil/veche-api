package com.veche.api.dto.votingSession

import java.time.Instant
import java.util.UUID

/**
 * TODO()
 *
 * @property name TODO()
 * @property partyId TODO()
 * @property discussionIds TODO()
 * @property firstRoundStartsAt TODO()
 * @property secondRoundStartsAt TODO()
 * @property endsAt TODO()
 */
data class VotingSessionRequestDto(
    val name: String,
    val partyId: UUID,
    val discussionIds: List<UUID>,
    val firstRoundStartsAt: Instant,
    val secondRoundStartsAt: Instant,
    val endsAt: Instant,
)
