package com.veche.api.database.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/**
 * TODO()
 *
 * @property subject TODO()
 * @property content TODO()
 * @property fileUrl TODO()
 * @property fileName TODO()
 * @property fileSize TODO()
 * @property party TODO()
 * @property creator TODO()
 * @property comments TODO()
 * @property summaries TODO()
 * @property votes TODO()
 * @property status TODO()
 * @property session TODO()
 * @property pendingActions TODO()
 */
@Entity
@Table(name = "discussions")
class DiscussionEntity : BaseEntity() {
    /** TODO() */
    @Column(name = "subject", nullable = false, length = 200)
    var subject: String = ""

    @Column(name = "content", nullable = false, length = 4000)
    var content: String = ""

    /** TODO() */
    @Column(name = "file_url", length = 500)
    var fileUrl: String? = null

    /** TODO() */
    @Column(name = "file_name")
    var fileName: String? = null

    /** TODO() */
    @Column(name = "file_size")
    var fileSize: Long? = null

    /** TODO() */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    lateinit var party: PartyEntity

    /** TODO() */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    lateinit var creator: UserEntity

    /** TODO() */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discussion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableSet<CommentEntity> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discussion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var summaries: MutableSet<SummaryEntity> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discussion", cascade = [CascadeType.ALL], orphanRemoval = true)
    var votes: MutableSet<DiscussionVoteEntity> = mutableSetOf()

    /** TODO() */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: DiscussionStatus = DiscussionStatus.WAITING

    /** TODO() */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = true)
    var session: VotingSessionEntity? = null

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discussion", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var pendingActions: MutableSet<PendingActionEntity> = mutableSetOf()
}

/**
 * TODO()
 */
enum class DiscussionStatus {
    /** TODO() */
    WAITING,
    /** TODO() */
    VOTING,
    /** TODO() */
    FINAL_VOTING,
    /** TODO() */
    RESOLVED,
    /** TODO() */
    ARCHIVED,
}
