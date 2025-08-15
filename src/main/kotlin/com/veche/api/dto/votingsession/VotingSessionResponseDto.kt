package com.veche.api.dto.votingsession

import com.veche.api.database.model.VotingSessionStatus
import com.veche.api.dto.discussion.DiscussionResponseDto
import com.veche.api.dto.party.PartyResponseDto
import java.time.Instant
import java.util.UUID

data class VotingSessionResponseDto(
    val id: UUID,
    val name: String,
    val party: PartyResponseDto,
    val status: VotingSessionStatus,
    val discussions: List<DiscussionResponseDto>,
    val firstRoundStart: Instant?,
    val secondRoundStart: Instant?,
    val endTime: Instant?,
)
