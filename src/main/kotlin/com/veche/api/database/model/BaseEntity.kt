package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

/**
 * TODO()
 *
 * @property id TODO()
 * @property createdAt TODO()
 * @property updatedAt TODO()
 * @property deletedAt TODO()
 */
@MappedSuperclass
abstract class BaseEntity {
    /** TODO() */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

    /** TODO() */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()

    /** TODO() */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    /** TODO() */
    @Column(name = "deleted_at", nullable = true)
    var deletedAt: Instant? = null
}
