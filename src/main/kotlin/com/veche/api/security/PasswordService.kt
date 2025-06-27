package com.veche.api.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService {

    private val encoder = BCryptPasswordEncoder()

    fun hash(rawPassword: String): String = encoder.encode(rawPassword)

    fun matches(rawPassword: String, hashedPassword: String): Boolean =
        encoder.matches(rawPassword, hashedPassword)

}