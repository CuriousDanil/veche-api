package com.veche.api.database.repository

import com.veche.api.database.model.PendingActionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface PendingActionRepository : JpaRepository<PendingActionEntity, UUID> {
    /**
     * TODO()
     *
     * @param discussionId TODO()
     * @return TODO()
     */
    fun findAllByDiscussionIdAndExecutedFalse(discussionId: UUID): List<PendingActionEntity>
}