package com.veche.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val error: String,
    val status: Int,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String,
)

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Invalid input", HttpStatus.BAD_REQUEST, request)

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        ex: BadCredentialsException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Invalid credentials", HttpStatus.UNAUTHORIZED, request)

    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(
        ex: SecurityException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Forbidden", HttpStatus.FORBIDDEN, request)

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        ex: IllegalStateException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request)

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR, request)

    private fun buildResponse(
        message: String,
        status: HttpStatus,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(
                error = message,
                status = status.value(),
                path = request.requestURI,
            ),
        )
}
