package com.veche.api.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

/**
 * TODO()
 *
 * @property encoder TODO()
 */
@Service
class PasswordService {
    private val encoder = BCryptPasswordEncoder()

    /**
     * TODO()
     *
     * @param rawPassword TODO()
     * @return TODO()
     */
    fun hash(rawPassword: String): String = encoder.encode(rawPassword)

    /**
     * TODO()
     *
     * @param rawPassword TODO()
     * @param hashedPassword TODO()
     * @return TODO()
     */
    fun matches(
        rawPassword: String,
        hashedPassword: String,
    ): Boolean = encoder.matches(rawPassword, hashedPassword)
}
