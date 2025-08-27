package com.veche.api.database.repository

import com.veche.api.database.model.SummaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface SummaryRepository : JpaRepository<SummaryEntity, UUID> {
    /**
     * TODO()
     *
     * @param discussionId TODO()
     * @return TODO()
     */
    fun findAllByDiscussionId(discussionId: UUID): List<SummaryEntity>
}
