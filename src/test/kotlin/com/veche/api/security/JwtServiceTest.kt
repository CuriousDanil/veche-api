package com.veche.api.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class JwtServiceTest {
    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setUp() {
        val secret = "cc0724944687a168de6429d3e86de951b7fa3804d83555d6e88c2ebfc2b61a36"
        jwtService = JwtService(secret)
    }

    @Test
    fun `generateAccessToken should create a valid access token`() {
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val partyIds = listOf(UUID.randomUUID(), UUID.randomUUID())

        val token =
            jwtService.generateAccessToken(
                userId = userId,
                companyId = companyId,
                partyIds = partyIds,
                canPostDiscussions = true,
                canManageSessions = false,
                canManageUsers = true,
            )

        assertTrue(jwtService.validateAccessToken(token))
        assertEquals(userId, jwtService.extractUserId(token))
    }

    @Test
    fun `generateRefreshToken should create a valid refresh token`() {
        val userId = UUID.randomUUID()
        val token = jwtService.generateRefreshToken(userId)

        assertTrue(jwtService.validateRefreshToken(token))
        assertEquals(userId, jwtService.extractUserId(token))
    }

    @Test
    fun `validateAccessToken should return false for invalid token`() {
        assertFalse(jwtService.validateAccessToken("invalid-token"))
    }

    @Test
    fun `validateAccessToken should return false for refresh token`() {
        val userId = UUID.randomUUID()
        val refreshToken = jwtService.generateRefreshToken(userId)
        assertFalse(jwtService.validateAccessToken(refreshToken))
    }

    @Test
    fun `validateRefreshToken should return false for invalid token`() {
        assertFalse(jwtService.validateRefreshToken("invalid-token"))
    }

    @Test
    fun `validateRefreshToken should return false for access token`() {
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val partyIds = listOf(UUID.randomUUID(), UUID.randomUUID())

        val accessToken =
            jwtService.generateAccessToken(
                userId = userId,
                companyId = companyId,
                partyIds = partyIds,
                canPostDiscussions = true,
                canManageSessions = false,
                canManageUsers = true,
            )
        assertFalse(jwtService.validateRefreshToken(accessToken))
    }
}
