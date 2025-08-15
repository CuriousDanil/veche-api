package com.veche.api.mapper

import com.veche.api.database.model.VotingSessionEntity
import com.veche.api.dto.votingSession.VotingSessionResponseDto
import org.springframework.stereotype.Component

@Component
class VotingSessionMapper(
    private val discussionMapper: DiscussionMapper,
    private val partyMapper: PartyMapper,
) {
    fun toDto(entity: VotingSessionEntity): VotingSessionResponseDto =
        VotingSessionResponseDto(
            id = entity.id,
            name = entity.name,
            party = partyMapper.toDto(entity.party),
            status = entity.status,
            discussions = entity.discussions.map { discussionMapper.toDto(it) },
            firstRoundStart = entity.firstRoundStartsAt,
            secondRoundStart = entity.secondRoundStartsAt,
            endTime = entity.endsAt,
        )
}
