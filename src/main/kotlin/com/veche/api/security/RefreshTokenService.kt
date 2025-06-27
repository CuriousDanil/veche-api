package com.veche.api.security

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class RefreshTokenService(
    private val redis: StringRedisTemplate,
    private val jwtService: JwtService,
) {
    private fun userKey(userId: UUID) = "refresh:$userId"

    private fun blackListKey(token: String) = "bl:$token"

    fun save(token: String) {
        val userId = jwtService.extractUserId(token)
        val timeToLive =
            Duration.between(
                Instant.now(),
                jwtService.extractExpiration(token),
            )
        if (timeToLive.isNegative || timeToLive.isZero) {
            throw IllegalArgumentException("Token is Invalid or expired")
        }
        redis.opsForValue().set(userKey(userId), token, timeToLive)
    }

    fun status(token: String): RefreshTokenStatus {
        if (!jwtService.validateRefreshToken(token)) {
            return RefreshTokenStatus.MALFORMED_OR_EXPIRED
        }

        if (redis.hasKey(blackListKey(token))) {
            return RefreshTokenStatus.BLACKLISTED
        }

        val userId = jwtService.extractUserId(token)
        val current = redis.opsForValue().get(userKey(userId))
        return if (token == current) {
            RefreshTokenStatus.VALID
        } else {
            RefreshTokenStatus.STALE // unreachable status during normal operation
        }
    }

    fun rotate(
        oldToken: String,
        newToken: String,
    ) {
        blacklist(oldToken)
        val userId = jwtService.extractUserId(oldToken)
        val timeToLive =
            Duration.between(
                Instant.now(),
                jwtService.extractExpiration(newToken),
            )
        if (timeToLive.isNegative || timeToLive.isZero) {
            throw IllegalArgumentException("Token is Invalid or expired")
        }
        redis.opsForValue().set(userKey(userId), newToken, timeToLive)
    }

    fun delete(token: String) {
        val userId = jwtService.extractUserId(token)
        redis.delete(userKey(userId))
        blacklist(token)
    }

    private fun blacklist(token: String) {
        val timeToLive =
            Duration.between(
                Instant.now(),
                jwtService.extractExpiration(token),
            )
        if (timeToLive.isNegative || timeToLive.isZero) return
        redis.opsForValue().set(blackListKey(token), "", timeToLive)
    }
}
