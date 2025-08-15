package com.veche.api.mapper

import com.veche.api.database.model.CommentEntity
import com.veche.api.dto.comment.CommentResponseDto
import org.springframework.stereotype.Component

@Component
class CommentMapper(
    private val userMapper: UserMapper,
) {
    fun toDto(entity: CommentEntity): CommentResponseDto =
        CommentResponseDto(
            id = entity.id,
            content = entity.content,
            creator = userMapper.toDto(entity.creator),
            status = entity.commentType,
            createdAt = entity.createdAt,
        )
}
