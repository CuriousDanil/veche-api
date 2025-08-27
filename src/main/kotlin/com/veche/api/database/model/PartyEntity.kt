package com.veche.api.database.model

import jakarta.persistence.*

/**
 * TODO()
 *
 * @property name TODO()
 * @property company TODO()
 * @property invitations TODO()
 * @property users TODO()
 * @property discussions TODO()
 * @property votingSessions TODO()
 */
@Entity
@Table(name = "parties")
class PartyEntity : BaseEntity() {
    @Column(name = "name", nullable = false)
    var name: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    lateinit var company: CompanyEntity

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var invitations: MutableSet<InvitationEntity> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "parties_users",
        joinColumns = [JoinColumn(name = "party_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    var users: MutableSet<UserEntity> = mutableSetOf()

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], orphanRemoval = true)
    var discussions: MutableSet<DiscussionEntity> = mutableSetOf()

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], orphanRemoval = true)
    var votingSessions: MutableSet<VotingSessionEntity> = mutableSetOf()
}
