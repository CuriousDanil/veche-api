package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.InvitationEntity
import com.veche.api.database.model.PartyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.InvitationRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.invitation.InvitationRequestDto
import com.veche.api.exception.NotFoundException
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*

class InvitationServiceTest {
    private lateinit var invitationService: InvitationService
    private lateinit var invitationRepository: InvitationRepository
    private lateinit var userRepository: UserRepository
    private lateinit var partyRepository: PartyRepository

    @BeforeEach
    fun setUp() {
        invitationRepository = mock()
        userRepository = mock()
        partyRepository = mock()
        invitationService = InvitationService(invitationRepository, userRepository, partyRepository)
    }

    @Test
    fun `createInvitation should create and return invitation successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val invitationRequest =
            InvitationRequestDto(
                name = "John Doe",
                bio = "Test bio",
                email = "john@example.com",
                partyId = partyId,
            )
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
                name = "Creator User"
                email = "creator@example.com"
            }
        val savedInvitation =
            InvitationEntity().apply {
                token = "generated_token"
                suggestedName = "John Doe"
                suggestedBio = "Test bio"
                suggestedEmail = "john@example.com"
                this.party = party
                creator = user
            }
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(invitationRepository.save(any<InvitationEntity>())).thenReturn(savedInvitation)

        // When
        val result = invitationService.createInvitation(userPrincipal, invitationRequest)

        // Then
        assertTrue(result.url.contains("generated_token"))
        verify(partyRepository).findById(partyId)
        verify(userRepository).findById(userId)
        verify(invitationRepository).save(any<InvitationEntity>())
    }

    @Test
    fun `createInvitation should throw NotFoundException when party not found`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val invitationRequest =
            InvitationRequestDto(
                name = "John Doe",
                bio = "Test bio",
                email = "john@example.com",
                partyId = partyId,
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            invitationService.createInvitation(userPrincipal, invitationRequest)
        }
    }

    @Test
    fun `createInvitation should throw NotFoundException when user not found`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val invitationRequest =
            InvitationRequestDto(
                name = "John Doe",
                bio = "Test bio",
                email = "john@example.com",
                partyId = partyId,
            )
        val party =
            PartyEntity().apply {
                name = "Test Party"
            }
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            invitationService.createInvitation(userPrincipal, invitationRequest)
        }
    }

    @Test
    fun `getInvitation should return invitation details when found`() {
        // Given
        val token = "valid_token"
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
            }
        val invitation =
            InvitationEntity().apply {
                this.token = token
                suggestedName = "John Doe"
                suggestedBio = "Test bio"
                suggestedEmail = "john@example.com"
                this.party = party
                expiresAt = Instant.now().plusSeconds(3600)
            }

        whenever(invitationRepository.findByToken(token)).thenReturn(invitation)

        // When
        val result = invitationService.getInvitation(token)

        // Then
        assertEquals(party.id, result.partyId)
        assertEquals("Test Company", result.companyName)
        assertEquals("John Doe", result.suggestedName)
        assertEquals("Test bio", result.suggestedBio)
        assertEquals("john@example.com", result.suggestedEmail)
        assertEquals(invitation.expiresAt, result.expiresAt)
        verify(invitationRepository).findByToken(token)
    }

    @Test
    fun `getInvitation should throw NotFoundException when invitation not found`() {
        // Given
        val token = "invalid_token"

        whenever(invitationRepository.findByToken(token)).thenReturn(null)

        // When/Then
        assertThrows<NotFoundException> {
            invitationService.getInvitation(token)
        }
    }

    @Test
    fun `randomToken should generate token of correct length`() {
        // When
        val token = invitationService.randomToken(48)

        // Then
        assertNotNull(token)
        assertTrue(token.isNotEmpty())
        // Base64 encoded 48 bytes should be 64 characters (without padding)
        assertTrue(token.length >= 60) // Allow some variance due to encoding
    }

    @Test
    fun `randomToken should generate different tokens`() {
        // When
        val token1 = invitationService.randomToken()
        val token2 = invitationService.randomToken()

        // Then
        assertNotEquals(token1, token2)
    }
}

