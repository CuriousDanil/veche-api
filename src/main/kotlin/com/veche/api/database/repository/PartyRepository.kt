package com.veche.api.database.repository

import com.veche.api.database.model.PartyEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * TODO()
 */
interface PartyRepository : JpaRepository<PartyEntity, UUID> {
    /**
     * TODO()
     *
     * @param companyId TODO()
     * @return TODO()
     */
    fun findAllByCompanyIdAndDeletedAtIsNull(companyId: UUID): List<PartyEntity>

    /**
     * TODO()
     *
     * @param partyId TODO()
     * @param userId TODO()
     * @return TODO()
     */
    fun existsByIdAndUsersId(
        partyId: UUID,
        userId: UUID,
    ): Boolean
}


