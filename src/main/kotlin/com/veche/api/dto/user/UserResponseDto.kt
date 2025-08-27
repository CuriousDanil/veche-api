package com.veche.api.dto.user

import com.veche.api.dto.party.PartyResponseDto
import java.util.UUID

/**
 * TODO()
 *
 * @property id TODO()
 * @property name TODO()
 * @property email TODO()
 * @property bio TODO()
 * @property parties TODO()
 * @property isAbleToPostDiscussions TODO()
 * @property isAbleToManageSessions TODO()
 * @property isAbleToManageUsers TODO()
 */
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