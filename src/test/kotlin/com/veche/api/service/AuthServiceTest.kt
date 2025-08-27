package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.InvitationEntity
import com.veche.api.database.model.PartyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.InvitationRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.auth.FounderRegistrationDto
import com.veche.api.dto.auth.LoginRequestDto
import com.veche.api.dto.auth.RefreshRequestDto
import com.veche.api.dto.auth.UserRegistrationDto
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.security.JwtService
import com.veche.api.security.PasswordService
import com.veche.api.security.RefreshTokenService
import com.veche.api.security.RefreshTokenStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.authentication.BadCredentialsException
import java.time.Instant
import java.util.*

class AuthServiceTest {
    private lateinit var authService: AuthService
    private lateinit var jwtService: JwtService
    private lateinit var userRepository: UserRepository
    private lateinit var passwordService: PasswordService
    private lateinit var companyRepository: CompanyRepository
    private lateinit var partyRepository: PartyRepository
    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var invitationRepository: InvitationRepository

    @BeforeEach
    fun setUp() {
        jwtService = mock()
        userRepository = mock()
        passwordService = mock()
        companyRepository = mock()
        partyRepository = mock()
        refreshTokenService = mock()
        invitationRepository = mock()

        authService =
            AuthService(
                jwtService,
                userRepository,
                passwordService,
                companyRepository,
                partyRepository,
                refreshTokenService,
                invitationRepository,
            )
    }

    @Test
    fun `registerFounder should create company, party and user successfully`() {
        // Given
        val dto =
            FounderRegistrationDto(
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                companyName = "Test Company",
                partyName = "Test Party",
            )
        val hashedPassword = "hashed_password"
        val savedCompany =
            CompanyEntity().apply {
                name = dto.companyName
            }
        val savedParty =
            PartyEntity().apply {
                name = dto.partyName
                company = savedCompany
            }
        val savedUser =
            UserEntity().apply {
                email = dto.email
                passwordHash = hashedPassword
                name = dto.name
                company = savedCompany
                parties = mutableSetOf(savedParty)
                isAbleToManageSessions = true
                isAbleToManageUsers = true
                isAbleToPostDiscussions = true
            }

        whenever(passwordService.hash(dto.password)).thenReturn(hashedPassword)
        whenever(companyRepository.save(any<CompanyEntity>())).thenReturn(savedCompany)
        whenever(partyRepository.save(any<PartyEntity>())).thenReturn(savedParty)
        whenever(userRepository.save(any<UserEntity>())).thenReturn(savedUser)

        // When
        val result = authService.registerFounder(dto)

        // Then
        assertEquals(dto.name, result.username)
        verify(passwordService).hash(dto.password)
        verify(companyRepository).save(any<CompanyEntity>())
        verify(partyRepository).save(any<PartyEntity>())
        verify(userRepository).save(any<UserEntity>())
    }

    @Test
    fun `registerUserByInvite should register user successfully with valid invitation`() {
        // Given
        val token = "valid_token"
        val partyId = UUID.randomUUID()
        val dto =
            UserRegistrationDto(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "password123",
                bio = "Test bio",
                partyId = partyId,
            )
        val hashedPassword = "hashed_password"
        val company = CompanyEntity().apply { name = "Test Company" }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
            }
        val invitation =
            InvitationEntity().apply {
                this.token = token
                expiresAt = Instant.now().plusSeconds(3600)
                usedAt = null
            }
        val savedUser =
            UserEntity().apply {
                email = dto.email
                passwordHash = hashedPassword
                name = dto.name
                bio = dto.bio
                parties = mutableSetOf(party)
                this.company = company
            }

        whenever(invitationRepository.findByToken(token)).thenReturn(invitation)
        whenever(passwordService.hash(dto.password)).thenReturn(hashedPassword)
        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.save(any<UserEntity>())).thenReturn(savedUser)

        // When
        val result = authService.registerUserByInvite(token, dto)

        // Then
        assertEquals(dto.name, result.username)
        verify(invitationRepository).findByToken(token)
        verify(passwordService).hash(dto.password)
        verify(partyRepository).findById(partyId)
        verify(userRepository).save(any<UserEntity>())
    }

    @Test
    fun `registerUserByInvite should throw exception for expired invitation`() {
        // Given
        val token = "expired_token"
        val dto =
            UserRegistrationDto(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "password123",
                bio = "Test bio",
                partyId = UUID.randomUUID(),
            )
        val invitation =
            InvitationEntity().apply {
                this.token = token
                expiresAt = Instant.now().minusSeconds(3600) // Expired
                usedAt = null
            }

        whenever(invitationRepository.findByToken(token)).thenReturn(invitation)

        // When/Then
        assertThrows<ForbiddenException> {
            authService.registerUserByInvite(token, dto)
        }
    }

    @Test
    fun `registerUserByInvite should throw exception for non-existent invitation`() {
        // Given
        val token = "invalid_token"
        val dto =
            UserRegistrationDto(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "password123",
                bio = "Test bio",
                partyId = UUID.randomUUID(),
            )

        whenever(invitationRepository.findByToken(token)).thenReturn(null)

        // When/Then
        assertThrows<NotFoundException> {
            authService.registerUserByInvite(token, dto)
        }
    }

    @Test
    fun `login should return token pair for valid credentials`() {
        // Given
        val dto =
            LoginRequestDto(
                email = "john@example.com",
                password = "password123",
            )
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val hashedPassword = "hashed_password"
        val accessToken = "access_token"
        val refreshToken = "refresh_token"

        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
            }
        val user =
            UserEntity().apply {
                email = dto.email
                passwordHash = hashedPassword
                name = "John Doe"
                this.company = company
                parties = mutableSetOf(party)
                isAbleToManageSessions = true
                isAbleToManageUsers = true
                isAbleToPostDiscussions = true
            }

        whenever(userRepository.findByEmail(dto.email)).thenReturn(user)
        whenever(passwordService.matches(dto.password, hashedPassword)).thenReturn(true)
        whenever(jwtService.generateAccessToken(any(), any(), any(), any(), any(), any())).thenReturn(accessToken)
        whenever(jwtService.generateRefreshToken(any())).thenReturn(refreshToken)

        // When
        val result = authService.login(dto)

        // Then
        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
        verify(userRepository).findByEmail(dto.email)
        verify(passwordService).matches(dto.password, hashedPassword)
        verify(jwtService).generateAccessToken(any(), any(), any(), any(), any(), any())
        verify(jwtService).generateRefreshToken(any())
        verify(refreshTokenService).save(refreshToken)
    }

    @Test
    fun `login should throw exception for non-existent user`() {
        // Given
        val dto =
            LoginRequestDto(
                email = "nonexistent@example.com",
                password = "password123",
            )

        whenever(userRepository.findByEmail(dto.email)).thenReturn(null)

        // When/Then
        assertThrows<BadCredentialsException> {
            authService.login(dto)
        }
    }

    @Test
    fun `login should throw exception for invalid password`() {
        // Given
        val dto =
            LoginRequestDto(
                email = "john@example.com",
                password = "wrong_password",
            )
        val hashedPassword = "hashed_password"
        val user =
            UserEntity().apply {
                email = dto.email
                passwordHash = hashedPassword
                name = "John Doe"
            }

        whenever(userRepository.findByEmail(dto.email)).thenReturn(user)
        whenever(passwordService.matches(dto.password, hashedPassword)).thenReturn(false)

        // When/Then
        assertThrows<BadCredentialsException> {
            authService.login(dto)
        }
    }

    @Test
    fun `refresh should return new token pair for valid refresh token`() {
        // Given
        val refreshToken = "valid_refresh_token"
        val newRefreshToken = "new_refresh_token"
        val accessToken = "new_access_token"
        val userId = UUID.randomUUID()
        val dto = RefreshRequestDto(refreshToken)

        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
            }
        val user =
            UserEntity().apply {
                email = "john@example.com"
                name = "John Doe"
                this.company = company
                parties = mutableSetOf(party)
                isAbleToManageSessions = true
                isAbleToManageUsers = true
                isAbleToPostDiscussions = true
            }

        whenever(refreshTokenService.status(refreshToken)).thenReturn(RefreshTokenStatus.VALID)
        whenever(jwtService.extractUserId(refreshToken)).thenReturn(userId)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(jwtService.generateRefreshToken(userId)).thenReturn(newRefreshToken)
        whenever(jwtService.generateAccessToken(any(), any(), any(), any(), any(), any())).thenReturn(accessToken)

        // When
        val result = authService.refresh(dto)

        // Then
        assertEquals(accessToken, result.accessToken)
        assertEquals(newRefreshToken, result.refreshToken)
        verify(refreshTokenService).status(refreshToken)
        verify(jwtService).extractUserId(refreshToken)
        verify(userRepository).findById(userId)
        verify(refreshTokenService).rotate(refreshToken, newRefreshToken)
    }

    @Test
    fun `refresh should throw SecurityException for blacklisted token`() {
        // Given
        val refreshToken = "blacklisted_token"
        val dto = RefreshRequestDto(refreshToken)

        whenever(refreshTokenService.status(refreshToken)).thenReturn(RefreshTokenStatus.BLACKLISTED)

        // When/Then
        assertThrows<SecurityException> {
            authService.refresh(dto)
        }
        verify(refreshTokenService).delete(refreshToken)
    }
}
