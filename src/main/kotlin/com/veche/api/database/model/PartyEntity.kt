package com.veche.api.database.model

import jakarta.persistence.*

/**
 * Represents a party within a company, encapsulating its name, associated company, users, and discussions.
 *
 * @property name The name of the party.
 * @property company The company to which the party belongs.
 * @property users The set of users associated with the party.
 * @property discussions The discussions linked to the party; cascades all operations and removes orphans.
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
