package com.veche.api.dto.discussion

import java.util.*

/**
 * Request body for creating a new discussion.
 *
 * @property subject The discussion subject or title.
 * @property partyId ID of the party this discussion belongs to (must be user's party).
 * @property fileUrl Optional URL of an attached file.
 * @property fileName Optional name of the attached file.
 * @property fileSize Optional size of the attached file in bytes.
 */
data class DiscussionRequestDto(
    val subject: String,
    val content: String,
    val partyId: UUID,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
)
