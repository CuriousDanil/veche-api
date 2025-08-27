package com.veche.api.database.repository

import com.veche.api.database.model.DiscussionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface DiscussionRepository : JpaRepository<DiscussionEntity, UUID> {
    /**
     * TODO()
     *
     * @param companyId TODO()
     * @return TODO()
     */
    fun findAllByPartyCompanyId(companyId: UUID): List<DiscussionEntity>
}