package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.PartyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.auth.FounderRegistrationDto
import com.veche.api.dto.auth.LoginRequestDto
import com.veche.api.dto.auth.RefreshRequestDto
import com.veche.api.dto.auth.RegistrationResponseDto
import com.veche.api.dto.auth.UserRegistrationDto
import com.veche.api.security.JwtService
import com.veche.api.security.PasswordService
import com.veche.api.security.RefreshTokenService
import com.veche.api.security.RefreshTokenStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordService,
    private val companyRepository: CompanyRepository,
    private val partyRepository: PartyRepository,
    private val refreshTokenService: RefreshTokenService,
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )

    @Transactional
    fun registerFounder(dto: FounderRegistrationDto): RegistrationResponseDto {
        val hashedPassword = passwordEncoder.hash(dto.password)
        val newCompany =
            companyRepository.save(
                CompanyEntity().apply { name = dto.companyName },
            )
        val newParty =
            partyRepository.save(
                PartyEntity().apply {
                    name = dto.partyName
                    company = newCompany
                },
            )
        val newUser =
            userRepository.save(
                UserEntity().apply {
                    email = dto.email
                    passwordHash = hashedPassword
                    name = dto.name
                    company = newCompany
                    parties = mutableSetOf(newParty)
                    isAbleToManageSessions = true
                    isAbleToManageUsers = true
                    isAbleToPostDiscussions = true
                },
            )

        newParty.users.add(newUser)

        return RegistrationResponseDto(newUser.name)
    }

    @Transactional
    fun registerUser(dto: UserRegistrationDto): RegistrationResponseDto {
        val hashedPassword = passwordEncoder.hash(dto.password)
        val newCompany =
            companyRepository.findById(dto.companyId).orElseThrow {
                IllegalArgumentException("Company with ID ${dto.companyId} not found.")
            }
        val party = partyRepository.findAllByCompanyIdAndDeletedAtIsNull(newCompany.id).first()
        val user =
            userRepository.save(
                UserEntity().apply {
                    email = dto.email
                    passwordHash = hashedPassword
                    name = dto.name
                    company = newCompany
                    parties = mutableSetOf(party)
                },
            )
        return RegistrationResponseDto(user.name)
    }

    @Transactional
    fun login(dto: LoginRequestDto): TokenPair {
        val user =
            userRepository.findByEmail(dto.email)
                ?: throw BadCredentialsException("Invalid credentials.")

        if (!passwordEncoder.matches(dto.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val accessToken =
            jwtService.generateAccessToken(
                userId = user.id,
                companyId = user.company.id,
                partyIds = user.parties.map { it.id },
                canPostDiscussions = user.isAbleToManageSessions,
                canManageSessions = user.isAbleToManageSessions,
                canManageUsers = user.isAbleToManageUsers,
            )
        val refreshToken = jwtService.generateRefreshToken(user.id)

        refreshTokenService.save(refreshToken)

        return TokenPair(accessToken, refreshToken)
    }

    @Transactional
    fun refresh(dto: RefreshRequestDto): TokenPair {
        when (refreshTokenService.status(dto.refreshToken)) {
            RefreshTokenStatus.VALID -> {
                val user =
                    userRepository.findById(jwtService.extractUserId(dto.refreshToken)).orElseThrow {
                        IllegalArgumentException("Invalid refresh token.")
                    }
                val newToken = jwtService.generateRefreshToken(user.id)
                refreshTokenService.rotate(dto.refreshToken, newToken)
                val accessToken =
                    jwtService.generateAccessToken(
                        userId = user.id,
                        companyId = user.company.id,
                        partyIds = user.parties.map { it.id },
                        canPostDiscussions = user.isAbleToManageSessions,
                        canManageSessions = user.isAbleToManageSessions,
                        canManageUsers = user.isAbleToManageUsers,
                    )
                return TokenPair(accessToken, newToken)
            }

            RefreshTokenStatus.BLACKLISTED -> {
                refreshTokenService.delete(dto.refreshToken)
                throw SecurityException("Access token invalid.")
            }

            RefreshTokenStatus.STALE -> {
                refreshTokenService.delete(dto.refreshToken)
                throw IllegalStateException("Critical error: Stale refresh token encountered.")
            }

            RefreshTokenStatus.MALFORMED_OR_EXPIRED -> throw IllegalArgumentException("Expired session.")
        }
    }
}
