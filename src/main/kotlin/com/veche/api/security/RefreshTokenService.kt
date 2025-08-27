package com.veche.api.security

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * TODO()
 *
 * @property redis TODO()
 * @property jwtService TODO()
 */
@Service
class RefreshTokenService(
    private val redis: StringRedisTemplate,
    private val jwtService: JwtService,
) {
    /**
     * TODO()
     *
     * @param userId TODO()
     * @return TODO()
     */
    private fun userKey(userId: UUID) = "refresh:$userId"

    /**
     * TODO()
     *
     * @param token TODO()
     * @return TODO()
     */
    private fun blackListKey(token: String) = "bl:$token"

    /**
     * TODO()
     *
     * @param token TODO()
     */
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

    /**
     * TODO()
     *
     * @param token TODO()
     * @return TODO()
     */
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

    /**
     * TODO()
     *
     * @param oldToken TODO()
     * @param newToken TODO()
     */
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

    /**
     * TODO()
     *
     * @param token TODO()
     */
    fun delete(token: String) {
        val userId = jwtService.extractUserId(token)
        redis.delete(userKey(userId))
        blacklist(token)
    }

    /**
     * TODO()
     *
     * @param token TODO()
     */
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
