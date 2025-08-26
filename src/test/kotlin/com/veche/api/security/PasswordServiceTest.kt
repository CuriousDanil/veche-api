package com.veche.api.security

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PasswordServiceTest {
    private val passwordService = PasswordService()

    @Test
    fun `hash should return a non-empty string`() {
        val hashedPassword = passwordService.hash("password")
        assertTrue(hashedPassword.isNotEmpty())
    }

    @Test
    fun `matches should return true for correct password`() {
        val rawPassword = "password"
        val hashedPassword = passwordService.hash(rawPassword)
        assertTrue(passwordService.matches(rawPassword, hashedPassword))
    }

    @Test
    fun `matches should return false for incorrect password`() {
        val rawPassword = "password"
        val hashedPassword = passwordService.hash(rawPassword)
        assertFalse(passwordService.matches("wrongpassword", hashedPassword))
    }
}
