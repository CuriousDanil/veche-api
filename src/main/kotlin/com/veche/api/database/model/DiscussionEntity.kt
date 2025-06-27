package com.veche.api.database.model

import jakarta.persistence.*

/**
 * Represents a discussion entity within the system.
 *
 * @property subject The subject or title of the discussion. Cannot be null and has a maximum length of 200 characters.
 * @property fileUrl Optional URL pointing to an associated file.
 * @property fileName Optional name of the associated file.
 * @property fileSize Optional size of the associated file in bytes.
 * @property party The party associated with this discussion. Cannot be null.
 * @property creator The user who created the discussion. Cannot be null.
 * @property comments Set of comments related to this discussion. Managed with cascade operations and orphan removal.
 * @property status The current status of the discussion. Defaults to [DiscussionStatus.WAITING].
 * @property session Optional session associated with the discussion.
 */
@Entity
@Table(name = "discussions")
data class DiscussionEntity(
    /**
     * The subject or title of the discussion.
     */
    @Column(name = "subject", nullable = false, length = 200)
    val subject: String,
    @Column(name = "content", nullable = false, length = 4000)
    val content: String,
    /**
     * Optional URL pointing to an associated file.
     */
    @Column(name = "file_url", length = 500)
    val fileUrl: String? = null,
    /**
     * Optional name of the associated file.
     */
    @Column(name = "file_name")
    val fileName: String? = null,
    /**
     * Optional size of the associated file in bytes.
     */
    @Column(name = "file_size")
    val fileSize: Long? = null,
    /**
     * The party associated with this discussion.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    val party: PartyEntity,
    /**
     * The user who created the discussion.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: UserEntity,
    /**
     * Set of comments related to this discussion.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discussion", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableSet<CommentEntity> = mutableSetOf(),
    /**
     * The current status of the discussion.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: DiscussionStatus = DiscussionStatus.WAITING,
    /**
     * Optional session associated with the discussion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = true)
    val session: SessionEntity? = null,
) : BaseEntity()

/**
 * Enumeration of possible statuses for a discussion.
 */
enum class DiscussionStatus {
    WAITING,
    VOTING,
    FINAL_VOTING,
    ARCHIVED,
}
