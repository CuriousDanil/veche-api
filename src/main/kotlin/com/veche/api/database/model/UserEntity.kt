package com.veche.api.database.model

import jakarta.persistence.*

/**
 * TODO()
 *
 * @property name TODO()
 * @property email TODO()
 * @property passwordHash TODO()
 * @property bio TODO()
 * @property isAbleToPostDiscussions TODO()
 * @property isAbleToManageSessions TODO()
 * @property isAbleToManageUsers TODO()
 * @property company TODO()
 * @property parties TODO()
 * @property discussionEntities TODO()
 * @property comments TODO()
 */
@Entity
@Table(name = "users")
class UserEntity : BaseEntity() {
    @Column(name = "name", nullable = false, length = 50)
    var name: String = ""

    @Column(name = "email", nullable = false)
    var email: String = ""

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = ""

    @Column(name = "bio", length = 400)
    var bio: String? = null

    @Column(name = "is_able_to_post_discussions")
    var isAbleToPostDiscussions: Boolean = false

    @Column(name = "is_able_to_manage_sessions")
    var isAbleToManageSessions: Boolean = false

    @Column(name = "is_able_to_manage_users")
    var isAbleToManageUsers: Boolean = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    lateinit var company: CompanyEntity

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    var parties: MutableSet<PartyEntity> = mutableSetOf()

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL], orphanRemoval = true)
    var discussionEntities: MutableSet<DiscussionEntity> = mutableSetOf()

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableSet<CommentEntity> = mutableSetOf()
}
