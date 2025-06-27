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
    indexes = [
        Index(
            name = "idx_discussion_votes_discussion_user_createdat",
            columnList = "discussion_id, user_id, created_at DESC",
        ),
    ],
)
data class DiscussionVoteEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discussion_id", nullable = false)
    val discussion: DiscussionEntity,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    @Enumerated(EnumType.STRING)
    @Column(name = "vote_value", nullable = false)
    val voteValue: VoteValue,
) : BaseEntity()

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
