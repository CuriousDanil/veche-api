package com.veche.api.database.model

import jakarta.persistence.*

/**
 * TODO()
 *
 * @property name TODO()
 * @property users TODO()
 * @property parties TODO()
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
