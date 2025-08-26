package com.veche.api.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.*

class RefreshTokenServiceTest {

    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var redis: StringRedisTemplate
    private lateinit var jwtService: JwtService
    private lateinit var valueOperations: ValueOperations<String, String>

    @BeforeEach
    fun setUp() {
        redis = mock()
        jwtService = mock()
        valueOperations = mock()
        whenever(redis.opsForValue()).thenReturn(valueOperations)
        refreshTokenService = RefreshTokenService(redis, jwtService)
    }

    @Test
    fun `status should return VALID for a valid token`() {
        val userId = UUID.randomUUID()
        val token = "valid-token"

        whenever(jwtService.validateRefreshToken(token)).thenReturn(true)
        whenever(redis.hasKey("bl:$token")).thenReturn(false)
        whenever(jwtService.extractUserId(token)).thenReturn(userId)
        whenever(valueOperations.get("refresh:$userId")).thenReturn(token)

        val status = refreshTokenService.status(token)

        assertEquals(RefreshTokenStatus.VALID, status)
    }

    @Test
    fun `status should return MALFORMED_OR_EXPIRED for an invalid token`() {
        val token = "invalid-token"
        whenever(jwtService.validateRefreshToken(token)).thenReturn(false)
        val status = refreshTokenService.status(token)
        assertEquals(RefreshTokenStatus.MALFORMED_OR_EXPIRED, status)
    }

    @Test
    fun `status should return BLACKLISTED for a blacklisted token`() {
        val token = "blacklisted-token"
        whenever(jwtService.validateRefreshToken(token)).thenReturn(true)
        whenever(redis.hasKey("bl:$token")).thenReturn(true)
        val status = refreshTokenService.status(token)
        assertEquals(RefreshTokenStatus.BLACKLISTED, status)
    }

    @Test
    fun `status should return STALE for a stale token`() {
        val userId = UUID.randomUUID()
        val token = "stale-token"
        val currentToken = "current-token"

        whenever(jwtService.validateRefreshToken(token)).thenReturn(true)
        whenever(redis.hasKey("bl:$token")).thenReturn(false)
        whenever(jwtService.extractUserId(token)).thenReturn(userId)
        whenever(valueOperations.get("refresh:$userId")).thenReturn(currentToken)

        val status = refreshTokenService.status(token)

        assertEquals(RefreshTokenStatus.STALE, status)
    }
}