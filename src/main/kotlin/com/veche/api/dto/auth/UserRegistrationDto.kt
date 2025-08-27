package com.veche.api.dto.auth

import java.util.UUID

/**
 * TODO()
 *
 * @property email TODO()
 * @property password TODO()
 * @property name TODO()
 * @property bio TODO()
 * @property partyId TODO()
 */
data class UserRegistrationDto(
    val email: String,
    val password: String,
    val name: String,
    val bio: String?,
    val partyId: UUID
)