package com.veche.api.dto.discussionVote

import com.veche.api.database.model.VoteValue
import java.util.UUID

data class DiscussionVoteResponseDto(
    val id: UUID,
    val authorId: UUID,
    val voteValue: VoteValue,
)
