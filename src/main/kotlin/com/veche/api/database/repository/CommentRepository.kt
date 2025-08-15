package com.veche.api.database.repository

import com.veche.api.database.model.CommentEntity
import com.veche.api.database.model.CommentType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CommentRepository : JpaRepository<CommentEntity, UUID> {
    fun findAllByDiscussionId(discussionId: UUID): List<CommentEntity>

    fun findAllByDiscussionIdAndCreatorId(
        discussionId: UUID,
        creatorId: UUID,
    ): List<CommentEntity>

    fun existsByDiscussionIdAndCreatorIdAndCommentType(
        discussionId: UUID,
        creatorId: UUID,
        commentType: CommentType,
    ): Boolean
}
