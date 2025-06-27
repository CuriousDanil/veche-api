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
class CompanyEntity : BaseEntity() {
    @Column(name = "name", nullable = false)
    var name: String = ""

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var users: MutableSet<UserEntity> = mutableSetOf()

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var parties: MutableSet<PartyEntity> = mutableSetOf()
}
