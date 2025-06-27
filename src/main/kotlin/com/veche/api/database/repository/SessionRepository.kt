package com.veche.api.database.repository

import com.veche.api.database.model.SessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SessionRepository : JpaRepository<SessionEntity, UUID> {
}