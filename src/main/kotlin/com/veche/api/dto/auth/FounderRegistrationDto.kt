package com.veche.api.dto.auth

data class FounderRegistrationDto(
    val email: String,
    val password: String,
    val name: String,
    val companyName: String,
    val partyName: String
)