package com.veche.api.security

enum class RefreshTokenStatus {
    VALID,
    BLACKLISTED,
    STALE,
    MALFORMED_OR_EXPIRED
}