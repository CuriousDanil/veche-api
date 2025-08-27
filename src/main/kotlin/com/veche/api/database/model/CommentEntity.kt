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
 * TODO()
 *
 * @property content TODO()
 * @property fileName TODO()
 * @property fileUrl TODO()
 * @property fileSize TODO()
 * @property discussion TODO()
 * @property creator TODO()
 * @property commentType TODO()
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

/**
 * TODO()
 */
enum class CommentType {
    /** TODO() */
    COMMENT,
    /** TODO() */
    ARGUMENT,
    /** TODO() */
    REVIEW
}
