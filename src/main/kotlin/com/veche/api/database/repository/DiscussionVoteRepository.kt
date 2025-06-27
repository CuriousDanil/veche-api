package com.veche.api.database.repository

import com.veche.api.database.model.DiscussionVoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DiscussionVoteRepository : JpaRepository<DiscussionVoteEntity, UUID> {
}