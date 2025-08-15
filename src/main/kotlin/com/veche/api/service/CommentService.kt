package com.veche.api.service

import com.veche.api.database.model.CommentEntity
import com.veche.api.database.model.CommentType
import com.veche.api.database.model.DiscussionStatus
import com.veche.api.database.repository.CommentRepository
import com.veche.api.database.repository.DiscussionRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.comment.CommentRequestDto
import com.veche.api.dto.comment.CommentResponseDto
import com.veche.api.dto.comment.CommentUpdateDto
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.CommentMapper
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val discussionRepository: DiscussionRepository,
    private val userRepository: UserRepository,
    private val commentMapper: CommentMapper,
) {
    @Transactional
    fun createComment(
        user: UserPrincipal,
        discussionId: UUID,
        request: CommentRequestDto,
    ): CommentResponseDto {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }
        val creator =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("User not found.") }

        val commentType =
            when (discussion.status) {
                DiscussionStatus.WAITING -> CommentType.COMMENT
                DiscussionStatus.VOTING -> CommentType.ARGUMENT
                DiscussionStatus.RESOLVED -> CommentType.REVIEW
                else -> throw ForbiddenException("Comments cannot be posted to discussions with status ${discussion.status}.")
            }

        if (commentRepository.existsByDiscussionIdAndCreatorIdAndCommentType(discussionId, user.id, commentType)) {
            throw ForbiddenException("You have already posted a ${commentType.name} comment on this discussion.")
        }

        val comment =
            CommentEntity().apply {
                this.content = request.content
                this.discussion = discussion
                this.creator = creator
                this.commentType = commentType
            }

        return commentMapper.toDto(commentRepository.save(comment))
    }

    @Transactional(readOnly = true)
    fun getCommentsForDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
    ): List<CommentResponseDto> {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val comments =
            when (discussion.status) {
                DiscussionStatus.WAITING -> {
                    if (discussion.creator.id == user.id) {
                        commentRepository
                            .findAllByDiscussionId(discussionId)
                            .filter { it.commentType == CommentType.COMMENT }
                    } else {
                        commentRepository
                            .findAllByDiscussionIdAndCreatorId(discussionId, user.id)
                            .filter { it.commentType == CommentType.COMMENT }
                    }
                }

                DiscussionStatus.VOTING -> {
                    commentRepository
                        .findAllByDiscussionIdAndCreatorId(discussionId, user.id)
                        .filter { it.commentType == CommentType.ARGUMENT }
                }

                DiscussionStatus.RESOLVED -> {
                    commentRepository
                        .findAllByDiscussionId(discussionId)
                        .filter { it.commentType == CommentType.ARGUMENT }
                }

                DiscussionStatus.ARCHIVED -> {
                    commentRepository.findAllByDiscussionId(discussionId)
                }

                else -> emptyList()
            }

        return comments.map(commentMapper::toDto)
    }

    @Transactional
    fun updateComment(
        user: UserPrincipal,
        commentId: UUID,
        updateDto: CommentUpdateDto,
    ): CommentResponseDto {
        val comment =
            commentRepository
                .findById(commentId)
                .orElseThrow { NotFoundException("Comment not found.") }

        if (comment.creator.id != user.id) {
            throw ForbiddenException("You can only update your own comments.")
        }

        val discussionStatus = comment.discussion.status
        val commentType = comment.commentType

        val isUpdatable =
            when {
                discussionStatus == DiscussionStatus.WAITING && commentType == CommentType.COMMENT -> true
                discussionStatus == DiscussionStatus.VOTING && commentType == CommentType.ARGUMENT -> true
                else -> false
            }

        if (!isUpdatable) {
            throw ForbiddenException("This comment cannot be updated at the current discussion status.")
        }

        comment.content = updateDto.content
        return commentMapper.toDto(commentRepository.save(comment))
    }
}
