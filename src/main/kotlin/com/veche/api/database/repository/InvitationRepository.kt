package com.veche.api.database.repository

import com.veche.api.database.model.InvitationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * TODO()
 */
interface InvitationRepository : JpaRepository<InvitationEntity, UUID> {
    /**
     * TODO()
     *
     * @param token TODO()
     * @return TODO()
     */
    fun findByToken(token: String): InvitationEntity?
}
