package com.veche.api.mapper

import com.veche.api.database.model.UserEntity
import com.veche.api.dto.user.UserResponseDto
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val partyMapper: PartyMapper,
) {
    fun toDto(entity: UserEntity) =
        UserResponseDto(
            id = entity.id,
            name = entity.name,
            bio = entity.bio ?: "",
            email = entity.email,
            parties = entity.parties.map(partyMapper::toDto),
            isAbleToPostDiscussions = entity.isAbleToPostDiscussions,
            isAbleToManageSessions = entity.isAbleToManageSessions,
            isAbleToManageUsers = entity.isAbleToManageUsers,
        )
}
