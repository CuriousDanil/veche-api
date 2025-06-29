package com.veche.api.mapper

import com.veche.api.database.model.DiscussionVoteEntity
import com.veche.api.dto.discussionVote.DiscussionVoteResponseDto
import org.springframework.stereotype.Component

@Component
class DiscussionVoteMapper {
    fun toDto(entity: DiscussionVoteEntity): DiscussionVoteResponseDto =
        DiscussionVoteResponseDto(
            id = entity.id,
            authorId = entity.user.id,
            voteValue = entity.voteValue,
        )
}
