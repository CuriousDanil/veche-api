package com.veche.api.dto.auth

data class LoginRequestDto(
    val email: String,
    val password: String
)
