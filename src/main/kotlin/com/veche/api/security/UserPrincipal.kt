package com.veche.api.security

import com.veche.api.database.model.UserEntity
import java.util.UUID

data class UserPrincipal(
    val id: UUID,
    val companyId: UUID,
    val partyIds: Set<UUID>,
    val isAbleToPostDiscussions: Boolean,
    val isAbleToManageSessions: Boolean,
    val isAbleToManageUsers: Boolean,
) {
    companion object {
        fun fromEntity(user: UserEntity): UserPrincipal {
            return UserPrincipal(
                id = user.id,
                companyId = user.company.id,
                partyIds = user.parties.map { it.id }.toSet(),
                isAbleToPostDiscussions = user.isAbleToPostDiscussions,
                isAbleToManageSessions = user.isAbleToManageSessions,
                isAbleToManageUsers = user.isAbleToManageUsers
            )
        }
    }
}