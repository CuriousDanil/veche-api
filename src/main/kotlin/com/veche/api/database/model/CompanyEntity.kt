package com.veche.api.database.model

import jakarta.persistence.*

/**
 * Entity representing a company.
 *
 * @property name the name of the company.
 * @property users the users belonging to the company; managed with cascade and orphanRemoval.
 * @property parties the parties associated with the company; managed with cascade and orphanRemoval.
 */
@Entity
@Table(name = "companies")
data class CompanyEntity(
    @Column(name = "name", nullable = false)
    val name: String,
    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val users: MutableSet<UserEntity> = mutableSetOf(),
    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val parties: MutableSet<PartyEntity> = mutableSetOf(),
) : BaseEntity()
