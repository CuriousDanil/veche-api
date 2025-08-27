package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

/**
 * TODO()
 *
 * @property name TODO()
 * @property firstRoundStartsAt TODO()
 * @property secondRoundStartsAt TODO()
 * @property endsAt TODO()
 * @property status TODO()
 * @property discussions TODO()
 * @property party TODO()
 */
@Entity
@Table(name = "voting_sessions")
class VotingSessionEntity : BaseEntity() {
    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "first_round_start_time")
    var firstRoundStartsAt: Instant? = null

    @Column(name = "second_round_start_time")
    var secondRoundStartsAt: Instant? = null

    @Column(name = "end_time")
    var endsAt: Instant? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: VotingSessionStatus = VotingSessionStatus.WAITING

    @OneToMany(mappedBy = "session", orphanRemoval = true)
    var discussions: MutableSet<DiscussionEntity> = mutableSetOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    lateinit var party: PartyEntity
}

/**
 * TODO()
 */
enum class VotingSessionStatus {
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