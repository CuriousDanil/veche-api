package com.veche.api.database.repository

import com.veche.api.database.model.InvitationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InvitationRepository : JpaRepository<InvitationEntity, UUID> {
    fun findByToken(token: String): InvitationEntity?
}
