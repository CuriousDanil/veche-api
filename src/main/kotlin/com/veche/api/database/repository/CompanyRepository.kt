package com.veche.api.database.repository

import com.veche.api.database.model.CompanyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface CompanyRepository : JpaRepository<CompanyEntity, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<CompanyEntity>
}

