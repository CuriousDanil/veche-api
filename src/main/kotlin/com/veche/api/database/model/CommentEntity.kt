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
 * @property content The textual content or content of the comment. Maximum length is 4000 characters.
 * @property fileName The name of the file attached to the comment, if any.
 * @property fileUrl The URL pointing to the file attached to the comment. Maximum length is 500 characters.
 * @property fileSize The size of the attached file in bytes.
 * @property discussion The discussion entity to which this comment belongs.
 * @property commentType The type of the comment.
 */
@Entity
@Table(name = "comments")
class CommentEntity : BaseEntity() {
    @Column(name = "content", length = 4000)
    var content: String = ""

    @Column(name = "file_name")
    var fileName: String? = null

    @Column(name = "file_url", length = 500)
    var fileUrl: String? = null

    @Column(name = "file_size")
    var fileSize: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    lateinit var discussion: DiscussionEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    lateinit var creator: UserEntity

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var commentType: CommentType = CommentType.COMMENT
}

enum class CommentType {
    COMMENT,
    ARGUMENT,
    REVIEW,
}
