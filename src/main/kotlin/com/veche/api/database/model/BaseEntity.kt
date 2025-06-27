package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy
import java.time.Instant
import java.util.*

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
     * This property must be manually assigned when the entity is created and must not be changed afterwards.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

    /**
     * Timestamp indicating when the entity was created.
     *
     * Set automatically to the instant of entity instantiation and is immutable thereafter.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()

    /**
     * Timestamp indicating when the entity was last updated.
     *
     * Initialized at instantiation and expected to be updated appropriately to reflect modifications.
     */
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()

    /**
     * Determines equality based on the entity's identifier and effective class type.
     *
     * This method correctly handles Hibernate proxy instances by comparing the underlying persistent classes.
     *
     * @param other the object to compare with
     * @return `true` if both objects represent the same entity instance, `false` otherwise
     */
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as BaseEntity

        return id == other.id
    }

    /**
     * Computes hash code consistent with [equals], based on the effective class type.
     *
     * Handles Hibernate proxy instances by using the underlying persistent class's hash code.
     *
     * @return hash code of the entity
     */
    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    /**
     * Returns a string representation of the entity, including its simple class name and [id].
     *
     * Useful for logging and debugging purposes to quickly identify entity instances.
     *
     * @return string representation of the entity
     */
    override fun toString(): String = this::class.simpleName + "(  id = $id )"
}
