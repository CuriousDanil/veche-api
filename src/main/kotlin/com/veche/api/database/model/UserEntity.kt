package com.veche.api.database.model

import jakarta.persistence.*

/**
 * Represents a user account within the system.
 *
 * @property name The user's display name.
 * @property email The user's unique email address.
 * @property passwordHash The securely stored hash of the user's password.
 * @property bio Optional user biography or profile description.
 * @property isAbleToPostDiscussions Indicates if the user is permitted to post new discussions.
 * @property isAbleToManageSessions Indicates if the user can manage sessions within the application.
 * @property isAbleToManageUsers Indicates if the user has permission to manage other user accounts.
 * @property company The company to which this user belongs.
 * @property parties The set of parties the user is a member of.
 * @property discussionEntities The discussions created by this user. Cascade operations and orphan removal are enabled.
 * @property comments The comments created by this user. Cascade operations and orphan removal are enabled.
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
