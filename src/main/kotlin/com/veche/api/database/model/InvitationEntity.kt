package com.veche.api.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "invitations",
    indexes = [Index(name = "idx_inv_token", columnList = "token", unique = true)],
)
class InvitationEntity : BaseEntity() {
    @Column(name = "token", nullable = false, unique = true, length = 64)
    lateinit var token: String

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "party_id", nullable = false)
    lateinit var party: PartyEntity

    @Column(name = "suggested_name", nullable = true, length = 50)
    var suggestedName: String? = null

    @Column(name = "suggested_bio", nullable = true, length = 400)
    var suggestedBio: String? = null

    @Column(name = "suggested_email", nullable = true)
    var suggestedEmail: String? = null

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = Instant.now().plusSeconds(6000)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    lateinit var creator: UserEntity

    @Column(name = "used_at", nullable = true)
    var usedAt: Instant? = null
}
