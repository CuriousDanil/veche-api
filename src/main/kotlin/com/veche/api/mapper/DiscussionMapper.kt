package com.veche.api.mapper

import com.veche.api.database.model.DiscussionEntity
import com.veche.api.dto.discussion.DiscussionConciseResponseDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import org.springframework.stereotype.Component

@Component
class DiscussionMapper(
    private val voteMapper: DiscussionVoteMapper
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
            partyId = entity.party.id,
            creatorName = entity.creator.name,
            status = entity.status,
            votes = entity.votes.map { voteMapper.toDto(it) },
        )

    fun toConciseDto(entity: DiscussionEntity): DiscussionConciseResponseDto =
        DiscussionConciseResponseDto(
            id = entity.id,
            subject = entity.subject,
            content = entity.content,
        )
}
