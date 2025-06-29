package com.veche.api.dto.discussion

import java.util.UUID

data class DiscussionConciseResponseDto(
    val id: UUID,
    val subject: String,
    val content: String
)