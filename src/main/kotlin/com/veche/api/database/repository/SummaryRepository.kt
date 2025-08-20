package com.veche.api.database.repository

import com.veche.api.database.model.SummaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SummaryRepository : JpaRepository<SummaryEntity, UUID> {
    fun findAllByDiscussionId(discussionId: UUID): List<SummaryEntity>
}
