package com.veche.api.exception

/**
 * TODO()
 *
 * @param message TODO()
 */
class NotFoundException(
    message: String,
) : RuntimeException(message)

/**
 * TODO()
 *
 * @param message TODO()
 */
class ForbiddenException(
    message: String,
) : RuntimeException(message)

/**
 * TODO()
 *
 * @param message TODO()
 */
class ConflictException(
    message: String,
) : RuntimeException(message)

/**
 * TODO()
 *
 * @param message TODO()
 */
class BadRequestException(
    message: String,
) : RuntimeException(message)
