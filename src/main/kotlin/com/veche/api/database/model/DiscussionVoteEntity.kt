package com.veche.api.database.model

import jakarta.persistence.*

/**
 * Represents a vote cast by a user on a discussion.
 *
 * @property discussion The discussion entity that this vote is associated with.
 * @property user The user entity who cast the vote.
 * @property voteValue The value of the vote, indicating agreement or disagreement.
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
 * Enum representing possible vote values.
 *
 * @property AGREE Indicates an agreement vote.
 * @property DISAGREE Indicates a disagreement vote.
 */
enum class VoteValue {
    AGREE,
    DISAGREE,
}
