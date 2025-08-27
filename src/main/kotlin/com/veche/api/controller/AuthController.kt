package com.veche.api.controller

import com.veche.api.dto.auth.FounderRegistrationDto
import com.veche.api.dto.auth.LoginRequestDto
import com.veche.api.dto.auth.RefreshRequestDto
import com.veche.api.dto.auth.RefreshResponseDto
import com.veche.api.dto.auth.RegistrationResponseDto
import com.veche.api.dto.auth.UserRegistrationDto
import com.veche.api.dto.invitation.InvitationResponseDto
import com.veche.api.service.AuthService
import com.veche.api.service.InvitationService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

/**
 * TODO()
 *
 * @property authService TODO()
 * @property invitationService TODO()
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val invitationService: InvitationService,
) {
    /**
     * TODO()
     *
     * @param request TODO()
     * @return TODO()
     */
    @PostMapping("/register/founder")
    fun registerFounder(
        @RequestBody request: FounderRegistrationDto,
    ): RegistrationResponseDto = authService.registerFounder(request)

    /**
     * TODO()
     *
     * @param token TODO()
     * @param request TODO()
     * @return TODO()
     */
    @PostMapping("/invitations/{token}/accept")
    fun registerUser(
        @PathVariable token: String,
        @RequestBody request: UserRegistrationDto,
    ): RegistrationResponseDto = authService.registerUserByInvite(token, request)

    /**
     * TODO()
     *
     * @param token TODO()
     * @return TODO()
     */
    @GetMapping("/invitations/{token}")
    fun getInvitation(
        @PathVariable token: String,
    ): InvitationResponseDto = invitationService.getInvitation(token)

    /**
     * TODO()
     *
     * @param request TODO()
     * @return TODO()
     */
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequestDto,
    ): ResponseEntity<RefreshResponseDto> {
        val (accessToken, refreshToken) = authService.login(request)
        val cookie =
            ResponseCookie
                .from("rt", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build()
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(RefreshResponseDto(accessToken))
    }

    /**
     * TODO()
     *
     * @param rt TODO()
     * @return TODO()
     */
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = "rt", required = false) rt: String?,
    ): ResponseEntity<RefreshResponseDto> {
        val (accessToken, refreshToken) = authService.refresh(RefreshRequestDto(rt ?: ""))
        val cookie =
            ResponseCookie
                .from("rt", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build()
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(RefreshResponseDto(accessToken))
    }
}
