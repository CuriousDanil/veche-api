package com.veche.api.database.repository

import com.veche.api.database.model.VotingSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VotingSessionRepository : JpaRepository<VotingSessionEntity, UUID>
