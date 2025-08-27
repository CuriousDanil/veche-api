package com.veche.api.dto.discussion

import java.util.*

/**
 * TODO()
 *
 * @property subject TODO()
 * @property content TODO()
 * @property partyId TODO()
 * @property fileUrl TODO()
 * @property fileName TODO()
 * @property fileSize TODO()
 */
data class DiscussionRequestDto(
    val subject: String,
    val content: String,
    val partyId: UUID,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
)
