package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Entity representing a comment within a discussion.
 *
 * @property subject The textual content or subject of the comment. Maximum length is 4000 characters.
 * @property fileName The name of the file attached to the comment, if any.
 * @property fileUrl The URL pointing to the file attached to the comment. Maximum length is 500 characters.
 * @property fileSize The size of the attached file in bytes.
 * @property discussion The discussion entity to which this comment belongs.
 * @property commentType The type of the comment.
 */
@Entity
@Table(name = "comments")
data class CommentEntity(
    /**
     * The textual content or subject of the comment.
     */
    @Column(name = "subject", length = 4000)
    val subject: String,
    /**
     * The name of the file attached to this comment.
     */
    @Column(name = "file_name")
    val fileName: String? = null,
    /**
     * The URL of the file attached to this comment.
     */
    @Column(name = "file_url", length = 500)
    val fileUrl: String? = null,
    /**
     * The size of the attached file in bytes.
     */
    @Column(name = "file_size")
    val fileSize: Long? = null,
    /**
     * The discussion to which this comment belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    val discussion: DiscussionEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: UserEntity,
    /**
     * The type of the comment.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val commentType: CommentType = CommentType.COMMENT,
) : BaseEntity()

enum class CommentType {
    COMMENT,
    ARGUMENT,
    REVIEW,
}
