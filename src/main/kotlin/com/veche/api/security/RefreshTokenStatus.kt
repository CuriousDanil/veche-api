package com.veche.api.security

/**
 * TODO()
 */
enum class RefreshTokenStatus {
    /** TODO() */
    VALID,
    /** TODO() */
    BLACKLISTED,
    /** TODO() */
    STALE,
    /** TODO() */
    MALFORMED_OR_EXPIRED
}