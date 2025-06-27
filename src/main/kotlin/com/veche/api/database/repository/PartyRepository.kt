package com.veche.api.database.repository

import com.veche.api.database.model.PartyEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PartyRepository : JpaRepository<PartyEntity, UUID> {
    fun findAllByCompanyId(companyId: UUID): List<PartyEntity>
}


