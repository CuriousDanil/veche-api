package com.veche.api.mapper

import com.veche.api.database.model.DiscussionEntity
import com.veche.api.dto.discussion.DiscussionConciseResponseDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import org.springframework.stereotype.Component

@Component
class DiscussionMapper(
    private val voteMapper: DiscussionVoteMapper,
    private val actionMapper: ActionMapper,
    private val partyMapper: PartyMapper,
) {
    fun toDto(entity: DiscussionEntity): DiscussionResponseDto =
        DiscussionResponseDto(
            id = entity.id,
            subject = entity.subject,
            content = entity.content,
            fileUrl = entity.fileUrl,
            fileName = entity.fileName,
            fileSize = entity.fileSize,
            createdAt = entity.createdAt,
            party = partyMapper.toDto(entity.party),
            creatorName = entity.creator.name,
            status = entity.status,
            votes = entity.votes.map { voteMapper.toDto(it) },
            actions = entity.pendingActions.map { actionMapper.toDto(it) },
        )

    fun toConciseDto(entity: DiscussionEntity): DiscussionConciseResponseDto =
        DiscussionConciseResponseDto(
            id = entity.id,
            subject = entity.subject,
            content = entity.content,
        )
}
