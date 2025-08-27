package com.veche.api.database.model

import jakarta.persistence.*

/**
 * TODO()
 *
 * @property discussion TODO()
 * @property user TODO()
 * @property voteValue TODO()
 */
@Entity
@Table(
    name = "discussion_votes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_discussion_user",
            columnNames = ["discussion_id", "user_id"],
        ),
    ],
)
class DiscussionVoteEntity : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discussion_id", nullable = false)
    lateinit var discussion: DiscussionEntity

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: UserEntity

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_value", nullable = false)
    var voteValue: VoteValue = VoteValue.AGREE
}

/**
 * TODO()
 */
enum class VoteValue {
    /** TODO() */
    AGREE,
    /** TODO() */
    DISAGREE,
}
