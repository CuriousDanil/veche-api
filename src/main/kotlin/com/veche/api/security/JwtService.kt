package com.veche.api.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.UUID

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
) {
    private val key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    private val accessTokenValidityMs = 5L * 60 * 1000
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000

    fun generateAccessToken(
        userId: UUID,
        companyId: UUID,
        partyIds: List<UUID>,
        canPostDiscussions: Boolean,
        canManageSessions: Boolean,
        canManageUsers: Boolean,
    ): String {
        val now = Instant.now()
        val expiry = now.plusMillis(accessTokenValidityMs)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .claim("tokenType", "access")
            .claim("companyId", companyId.toString())
            .claim("partyIds", partyIds.map(UUID::toString))
            .claim("canPostDiscussions", canPostDiscussions)
            .claim("canManageSessions", canManageSessions)
            .claim("canManageUsers", canManageUsers)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: UUID): String {
        val now = Instant.now()
        val expiry = now.plusMillis(refreshTokenValidityMs)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .claim("tokenType", "refresh")
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    private fun parseClaims(token: String): Claims? =
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (ex: Exception) {
            null
        }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseClaims(token) ?: return false
        val tokenType = claims["tokenType"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseClaims(token) ?: return false
        val tokenType = claims["tokenType"] as? String ?: return false
        return tokenType == "refresh"
    }

    fun extractUserId(token: String): UUID {
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        return UUID.fromString(claims.subject)
    }

    fun extractExpiration(token: String): Instant {
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        return claims.expiration.toInstant()
    }
}
