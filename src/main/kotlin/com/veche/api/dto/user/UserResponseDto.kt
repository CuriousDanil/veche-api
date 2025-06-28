package com.veche.api.dto.user

import com.veche.api.dto.party.PartyResponseDto
import java.util.UUID

data class UserResponseDto(
    val id: UUID,
    val name: String,
    val email: String,
    val bio: String,
    val parties: List<PartyResponseDto>,
    val isAbleToPostDiscussions: Boolean,
    val isAbleToManageSessions: Boolean,
    val isAbleToManageUsers: Boolean,
)