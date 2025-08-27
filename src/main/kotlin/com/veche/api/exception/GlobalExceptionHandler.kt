package com.veche.api.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.LocalDateTime

/**
 * TODO()
 *
 * @property error TODO()
 * @property status TODO()
 * @property timestamp TODO()
 * @property path TODO()
 * @property details TODO()
 */
data class ErrorResponse(
    val error: String,
    val status: Int,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String,
    val details: String? = null,
)

/**
 * TODO()
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // === Custom Application Exceptions ===

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        ex: NotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Resource not found", HttpStatus.NOT_FOUND, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(
        ex: BadRequestException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Bad request", HttpStatus.BAD_REQUEST, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(
        ex: ForbiddenException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Access forbidden", HttpStatus.FORBIDDEN, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        ex: ConflictException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Resource conflict", HttpStatus.CONFLICT, request)

    // === Spring Security Exceptions ===

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        ex: BadCredentialsException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFound(
        ex: UsernameNotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("User not found", HttpStatus.UNAUTHORIZED, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(
        ex: SecurityException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Access denied", HttpStatus.FORBIDDEN, request)

    // === Validation Exceptions ===

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.joinToString("; ") {
                "${it.field}: ${it.defaultMessage}"
            }
        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, request, errors)
    }

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse(ex.message ?: "Invalid input", HttpStatus.BAD_REQUEST, request)

    // === HTTP/Web Exceptions ===

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMalformedJson(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Malformed JSON request", HttpStatus.BAD_REQUEST, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameter(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Missing required parameter: ${ex.parameterName}", HttpStatus.BAD_REQUEST, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Invalid parameter type for '${ex.name}'", HttpStatus.BAD_REQUEST, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Method ${ex.method} not allowed", HttpStatus.METHOD_NOT_ALLOWED, request)

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(
        ex: NoResourceFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> = buildResponse("Endpoint not found", HttpStatus.NOT_FOUND, request)

    // === Server Errors ===

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        ex: IllegalStateException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Illegal state exception: ${ex.message}", ex)
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request)
    }

    /**
     * TODO()
     *
     * @param ex TODO()
     * @param request TODO()
     * @return TODO()
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ${ex.message}", ex)
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request)
    }

    /**
     * TODO()
     *
     * @param message TODO()
     * @param status TODO()
     * @param request TODO()
     * @param details TODO()
     * @return TODO()
     */
    private fun buildResponse(
        message: String,
        status: HttpStatus,
        request: HttpServletRequest,
        details: String? = null,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(
                error = message,
                status = status.value(),
                path = request.requestURI,
                details = details,
            ),
        )
}
