package com.veche.api.dto.auth

/**
 * TODO()
 *
 * @property email TODO()
 * @property password TODO()
 * @property name TODO()
 * @property companyName TODO()
 * @property partyName TODO()
 */
data class FounderRegistrationDto(
    val email: String,
    val password: String,
    val name: String,
    val companyName: String,
    val partyName: String
)