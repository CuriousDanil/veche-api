package com.veche.api.database.repository

import com.veche.api.database.model.CommentEntity
import com.veche.api.database.model.CommentType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface CommentRepository : JpaRepository<CommentEntity, UUID> {
    /**
     * TODO()
     *
     * @param discussionId TODO()
     * @return TODO()
     */
    fun findAllByDiscussionId(discussionId: UUID): List<CommentEntity>

    /**
     * TODO()
     *
     * @param discussionId TODO()
     * @param creatorId TODO()
     * @return TODO()
     */
    fun findAllByDiscussionIdAndCreatorId(
        discussionId: UUID,
        creatorId: UUID,
    ): List<CommentEntity>

    /**
     * TODO()
     *
     * @param discussionId TODO()
     * @param creatorId TODO()
     * @param commentType TODO()
     * @return TODO()
     */
    fun existsByDiscussionIdAndCreatorIdAndCommentType(
        discussionId: UUID,
        creatorId: UUID,
        commentType: CommentType,
    ): Boolean
}
