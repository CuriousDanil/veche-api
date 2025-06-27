package com.veche.api.controller

import com.veche.api.dto.auth.FounderRegistrationDto
import com.veche.api.dto.auth.LoginRequestDto
import com.veche.api.dto.auth.RefreshRequestDto
import com.veche.api.dto.auth.RegistrationResponseDto
import com.veche.api.dto.auth.UserRegistrationDto
import com.veche.api.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register/founder")
    fun registerFounder(
        @RequestBody request: FounderRegistrationDto,
    ): RegistrationResponseDto = authService.registerFounder(request)

    @PostMapping("/register/user")
    fun registerUser(
        @RequestBody request: UserRegistrationDto,
    ): RegistrationResponseDto = authService.registerUser(request)

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequestDto,
    ): AuthService.TokenPair = authService.login(request)

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody request: RefreshRequestDto,
    ): AuthService.TokenPair = authService.refresh(request)
}
