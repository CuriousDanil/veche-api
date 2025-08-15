package com.veche.api.dto.discussion

import com.veche.api.database.model.DiscussionStatus
import com.veche.api.dto.action.ActionResponseDto
import com.veche.api.dto.discussionVote.DiscussionVoteResponseDto
import com.veche.api.dto.party.PartyResponseDto
import java.time.Instant
import java.util.*

/**
 * Response DTO representing a discussion returned by the API.
 *
 * @property id ID of the discussion.
 * @property subject Subject or title of the discussion.
 * @property partyId ID of the party the discussion belongs to.
 * @property creatorName Name of the user who created the discussion.
 * @property fileUrl Optional URL of an attached file.
 * @property fileName Optional name of an attached file.
 * @property fileSize Optional size of an attached file in bytes.
 * @property status Current status of the discussion.
 */
data class DiscussionResponseDto(
    val id: UUID,
    val subject: String,
    val content: String,
    val createdAt: Instant,
    val party: PartyResponseDto,
    val creatorName: String,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val status: DiscussionStatus = DiscussionStatus.WAITING,
    val votes: List<DiscussionVoteResponseDto>,
    val actions: List<ActionResponseDto>,
)
