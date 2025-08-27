package com.veche.api.database.repository

import com.veche.api.database.model.VotingSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface VotingSessionRepository : JpaRepository<VotingSessionEntity, UUID> {
    /**
     * TODO()
     *
     * @param companyId TODO()
     * @return TODO()
     */
    fun findAllByPartyCompanyId(companyId: UUID): List<VotingSessionEntity>
}
