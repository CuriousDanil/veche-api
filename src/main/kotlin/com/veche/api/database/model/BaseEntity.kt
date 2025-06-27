package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

/**
 * Abstract base class for all persistent entities, providing common identifier and timestamp properties.
 *
 * Entities extending this class must manually assign a unique [id] upon creation.
 * The [createdAt] and [updatedAt] timestamps are initialized at instantiation to the current instant.
 *
 * Equality and hash code calculations are based solely on the entity's [id] and its effective class type,
 * taking into account potential Hibernate proxy instances to ensure consistent behavior.
 */
@MappedSuperclass
abstract class BaseEntity {
    /**
     * Unique identifier for the entity.
     *
     * This property must be manually assigned when the entity is created and must not be changed afterward.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

    /**
     * Timestamp indicating when the entity was created.
     *
     * Set automatically to the instant of entity instantiation and is immutable thereafter.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()

    /**
     * Timestamp indicating when the entity was last updated.
     *
     * Initialized at instantiation and expected to be updated appropriately to reflect modifications.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    /**
     * Timestamp indicating when the entity was soft deleted.
     *
     * Null for active entities, set to deletion time when entity is soft deleted.
     */
    @Column(name = "deleted_at", nullable = true)
    var deletedAt: Instant? = null
}
