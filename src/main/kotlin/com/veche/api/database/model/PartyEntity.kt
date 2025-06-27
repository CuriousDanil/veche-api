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
data class PartyEntity (

    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    val company: CompanyEntity,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "parties_users",
        joinColumns = [JoinColumn(name = "party_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val users: MutableSet<UserEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "party", cascade = [CascadeType.ALL], orphanRemoval = true)
    val discussions: MutableSet<DiscussionEntity> = mutableSetOf()

): BaseEntity()