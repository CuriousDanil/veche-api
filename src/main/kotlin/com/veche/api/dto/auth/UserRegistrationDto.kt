package com.veche.api.dto.auth

import java.util.UUID

data class UserRegistrationDto(
    val email: String,
    val password: String,
    val name: String,
    val companyId: UUID
)