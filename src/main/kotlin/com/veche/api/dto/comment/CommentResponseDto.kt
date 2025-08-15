package com.veche.api.dto.comment

import com.veche.api.database.model.CommentType
import com.veche.api.dto.user.UserResponseDto
import java.time.Instant
import java.util.UUID

data class CommentResponseDto(
    val id: UUID,
    val content: String,
    val creator: UserResponseDto,
    val status: CommentType,
    val createdAt: Instant,
)
