package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.PartyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.party.PartyRequestDto
import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.dto.party.PartyUpdateDto
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.PartyMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*

class PartyServiceTest {
    private lateinit var partyService: PartyService
    private lateinit var companyRepository: CompanyRepository
    private lateinit var partyRepository: PartyRepository
    private lateinit var partyMapper: PartyMapper
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        companyRepository = mock()
        partyRepository = mock()
        partyMapper = mock()
        userRepository = mock()
        partyService = PartyService(companyRepository, partyRepository, partyMapper, userRepository)
    }

    @Test
    fun `createParty should create and return party successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val partyRequest = PartyRequestDto(name = "Test Party")
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }
        val savedParty =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
                users = mutableSetOf(user)
            }
        val partyResponse =
            PartyResponseDto(
                id = UUID.randomUUID(),
                name = "Test Party",
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = companyId,
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(companyRepository.getReferenceById(companyId)).thenReturn(company)
        whenever(userRepository.getReferenceById(userId)).thenReturn(user)
        whenever(partyRepository.save(any<PartyEntity>())).thenReturn(savedParty)
        whenever(partyMapper.toDto(savedParty)).thenReturn(partyResponse)

        // When
        val result = partyService.createParty(partyRequest, userPrincipal)

        // Then
        assertEquals(partyResponse, result)
        verify(companyRepository).getReferenceById(companyId)
        verify(userRepository).getReferenceById(userId)
        verify(partyRepository).save(any<PartyEntity>())
        verify(partyMapper).toDto(savedParty)
    }

    @Test
    fun `updateParty should update and return party`() {
        // Given
        val partyId = UUID.randomUUID()
        val updateDto = PartyUpdateDto(name = "Updated Party")
        val party =
            PartyEntity().apply {
                name = "Original Party"
            }
        val partyResponse =
            PartyResponseDto(
                id = partyId,
                name = "Updated Party",
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(partyMapper.toDto(party)).thenReturn(partyResponse)

        // When
        val result = partyService.updateParty(updateDto, partyId)

        // Then
        assertEquals(partyResponse, result)
        assertEquals("Updated Party", party.name)
        verify(partyRepository).findById(partyId)
        verify(partyMapper).toDto(party)
    }

    @Test
    fun `updateParty should throw NotFoundException when party not found`() {
        // Given
        val partyId = UUID.randomUUID()
        val updateDto = PartyUpdateDto(name = "Updated Party")

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            partyService.updateParty(updateDto, partyId)
        }
    }

    @Test
    fun `getAllPartiesForUserCompany should return all parties for user's company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val party1 =
            PartyEntity().apply {
                name = "Party 1"
            }
        val party2 =
            PartyEntity().apply {
                name = "Party 2"
            }
        val parties = listOf(party1, party2)
        val partyResponses =
            listOf(
                PartyResponseDto(UUID.randomUUID(), "Party 1"),
                PartyResponseDto(UUID.randomUUID(), "Party 2"),
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = companyId,
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId)).thenReturn(parties)
        whenever(partyMapper.toDto(party1)).thenReturn(partyResponses[0])
        whenever(partyMapper.toDto(party2)).thenReturn(partyResponses[1])

        // When
        val result = partyService.getAllPartiesForUserCompany(userPrincipal)

        // Then
        assertEquals(2, result.size)
        assertEquals(partyResponses, result)
        verify(partyRepository).findAllByCompanyIdAndDeletedAtIsNull(companyId)
        verify(partyMapper, times(2)).toDto(any<PartyEntity>())
    }

    @Test
    fun `getPartyById should return party when user belongs to same company`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
                this.company = company
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
            }
        val partyResponse =
            PartyResponseDto(
                id = partyId,
                name = "Test Party",
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

        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(partyMapper.toDto(party)).thenReturn(partyResponse)

        // When
        val result = partyService.getPartyById(partyId, userPrincipal)

        // Then
        assertEquals(partyResponse, result)
        verify(userRepository).findById(userId)
        verify(partyRepository).findById(partyId)
        verify(partyMapper).toDto(party)
    }

    @Test
    fun `getPartiesForUser should return parties for user's party IDs`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId1 = UUID.randomUUID()
        val partyId2 = UUID.randomUUID()
        val party1 =
            PartyEntity().apply {
                name = "User Party 1"
            }
        val party2 =
            PartyEntity().apply {
                name = "User Party 2"
            }
        val parties = listOf(party1, party2)
        val partyResponses =
            listOf(
                PartyResponseDto(partyId1, "User Party 1"),
                PartyResponseDto(partyId2, "User Party 2"),
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(partyId1, partyId2),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findAllById(setOf(partyId1, partyId2))).thenReturn(parties)
        whenever(partyMapper.toDto(party1)).thenReturn(partyResponses[0])
        whenever(partyMapper.toDto(party2)).thenReturn(partyResponses[1])

        // When
        val result = partyService.getPartiesForUser(userPrincipal)

        // Then
        assertEquals(2, result.size)
        assertEquals(partyResponses, result)
        verify(partyRepository).findAllById(setOf(partyId1, partyId2))
        verify(partyMapper, times(2)).toDto(any<PartyEntity>())
    }

    @Test
    fun `deleteParty should soft delete party`() {
        // Given
        val partyId = UUID.randomUUID()
        val party =
            PartyEntity().apply {
                name = "Test Party"
                deletedAt = null
            }

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))

        // When
        partyService.deleteParty(partyId)

        // Then
        assertNotNull(party.deletedAt)
        verify(partyRepository).findById(partyId)
    }

    @Test
    fun `deleteParty should throw NotFoundException when party not found`() {
        // Given
        val partyId = UUID.randomUUID()

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            partyService.deleteParty(partyId)
        }
    }

    @Test
    fun `addUserToParty should add user to party when not already a member`() {
        // Given
        val partyId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val company =
            CompanyEntity().apply {
                name = "Test Company"
                users = mutableSetOf()
            }
        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
                this.company = company
                parties = mutableSetOf()
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                this.company = company
                users = mutableSetOf()
            }

        whenever(partyRepository.existsByIdAndUsersId(partyId, userId)).thenReturn(false)
        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        // When
        partyService.addUserToParty(partyId, userId)

        // Then
        assertTrue(party.users.contains(user))
        assertTrue(user.parties.contains(party))
        verify(partyRepository).existsByIdAndUsersId(partyId, userId)
        verify(partyRepository).findById(partyId)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `addUserToParty should be idempotent when user already a member`() {
        // Given
        val partyId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        whenever(partyRepository.existsByIdAndUsersId(partyId, userId)).thenReturn(true)

        // When
        partyService.addUserToParty(partyId, userId)

        // Then
        verify(partyRepository).existsByIdAndUsersId(partyId, userId)
        verify(partyRepository, never()).findById(any())
        verify(userRepository, never()).findById(any())
    }

    @Test
    fun `evictUserFromParty should remove user from party when they are a member`() {
        // Given
        val partyId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
                parties = mutableSetOf()
            }
        val party =
            PartyEntity().apply {
                name = "Test Party"
                users = mutableSetOf(user)
            }
        user.parties.add(party)

        whenever(partyRepository.existsByIdAndUsersId(partyId, userId)).thenReturn(true)
        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.getReferenceById(userId)).thenReturn(user)

        // When
        partyService.evictUserFromParty(partyId, userId)

        // Then
        assertFalse(party.users.contains(user))
        assertFalse(user.parties.contains(party))
        verify(partyRepository).existsByIdAndUsersId(partyId, userId)
        verify(partyRepository).findById(partyId)
        verify(userRepository).getReferenceById(userId)
    }

    @Test
    fun `evictUserFromParty should be idempotent when user not a member`() {
        // Given
        val partyId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        whenever(partyRepository.existsByIdAndUsersId(partyId, userId)).thenReturn(false)

        // When
        partyService.evictUserFromParty(partyId, userId)

        // Then
        verify(partyRepository).existsByIdAndUsersId(partyId, userId)
        verify(partyRepository, never()).findById(any())
        verify(userRepository, never()).getReferenceById(any())
    }
}

