package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.web.bind.support.SessionStatus
import java.time.Instant

@Entity
@Table(name = "voting_sessions")
class VotingSessionEntity : BaseEntity() {
    @Column(name = "first_round_start_time")
    var firstRoundStartsAt: Instant? = null

    @Column(name = "second_round_start_time")
    var secondRoundStartsAt: Instant? = null

    @Column(name = "end_time")
    var endsAt: Instant? = null

    @Column(name = "status")
    var status: VotingSessionStatus = VotingSessionStatus.WAITING

    @OneToMany(mappedBy = "session", orphanRemoval = true)
    var discussions: MutableSet<DiscussionEntity> = mutableSetOf()
}

enum class VotingSessionStatus {
    WAITING,
    VOTING,
    FINAL_VOTING,
    RESOLVED,
    ARCHIVED,
}
