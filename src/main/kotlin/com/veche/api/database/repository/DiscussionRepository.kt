package com.veche.api.database.repository

import com.veche.api.database.model.DiscussionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DiscussionRepository : JpaRepository<DiscussionEntity, UUID> {
    fun findAllByPartyCompanyId(companyId: UUID): List<DiscussionEntity>
}