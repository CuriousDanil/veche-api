package com.veche.api.database.model

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type

/**
 * TODO()
 *
 * @property discussion TODO()
 * @property actionType TODO()
 * @property payload TODO()
 * @property executed TODO()
 */
@Entity
@Table(name = "pending_actions")
class PendingActionEntity : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    lateinit var discussion: DiscussionEntity

    @Enumerated(EnumType.STRING)
    lateinit var actionType: ActionType

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb", nullable = false)
    lateinit var payload: String

    @Column(name = "executed", nullable = false)
    var executed: Boolean = false
}

/**
 * TODO()
 */
enum class ActionType {
    /** TODO() */
    RENAME_PARTY,
    /** TODO() */
    RENAME_COMPANY,
    /** TODO() */
    EVICT_USER_FROM_PARTY,
    /** TODO() */
    ADD_USER_TO_PARTY,
    /** TODO() */
    DELETE_PARTY
}