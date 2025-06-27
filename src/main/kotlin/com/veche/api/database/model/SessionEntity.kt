package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

/**
 * Represents a user session with an optional expiration timestamp.
 *
 * @property expiresAt The timestamp when the session expires, or null if it does not expire.
 * @property discussions The set of discussions associated with this session; cascades all operations and removes orphans.
 */
@Entity
@Table(name = "sessions")
data class SessionEntity(

    @Column(name = "expires_at")
    val expiresAt: Instant? = null,

    @OneToMany(mappedBy = "session", orphanRemoval = true)
    val discussions: MutableSet<DiscussionEntity> = mutableSetOf()

) : BaseEntity()