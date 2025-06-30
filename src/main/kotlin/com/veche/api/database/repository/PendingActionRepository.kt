package com.veche.api.database.repository

import com.veche.api.database.model.PendingActionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PendingActionRepository : JpaRepository<PendingActionEntity, UUID> {
    fun findAllByDiscussionIdAndExecutedFalse(discussionId: UUID): List<PendingActionEntity>
}