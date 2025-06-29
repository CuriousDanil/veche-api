package com.veche.api.database.repository

import com.veche.api.database.model.DiscussionVoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface DiscussionVoteRepository : JpaRepository<DiscussionVoteEntity, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            INSERT INTO discussion_votes
                  (id, discussion_id, user_id, vote_value, created_at, updated_at)
            VALUES (
                     :#{#vote.id},
                     :#{#vote.discussion.id},
                     :#{#vote.user.id},
                     :#{#vote.voteValue.name()},
                     :#{#vote.createdAt},
                     :#{#vote.updatedAt}
                   )
            ON CONFLICT (discussion_id, user_id)
            DO UPDATE
               SET vote_value = EXCLUDED.vote_value,
                   updated_at = EXCLUDED.updated_at
        """,
        nativeQuery = true
    )
    fun upsertVote(@Param("vote") vote: DiscussionVoteEntity)
}
