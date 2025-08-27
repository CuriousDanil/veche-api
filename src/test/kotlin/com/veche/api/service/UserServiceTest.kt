package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.user.UserResponseDto
import com.veche.api.dto.user.UserSearchRequestDto
import com.veche.api.dto.user.UserUpdateCredentialsDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.UserMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*

class UserServiceTest {
    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var userMapper: UserMapper

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        userMapper = mock()
        userService = UserService(userRepository, userMapper)
    }

    @Test
    fun `getUserById should return user when both users are in same company`() {
        // Given
        val authenticatedUserId = UUID.randomUUID()
        val targetUserId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val authenticatedUser =
            UserEntity().apply {
                name = "Auth User"
                email = "auth@example.com"
                this.company = company
            }
        val targetUser =
            UserEntity().apply {
                name = "Target User"
                email = "target@example.com"
                this.company = company
            }
        val userResponseDto =
            UserResponseDto(
                id = targetUserId,
                name = "Target User",
                email = "target@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )
        val userPrincipal =
            UserPrincipal(
                id = authenticatedUserId,
                companyId = companyId,
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser))
        whenever(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(authenticatedUser))
        whenever(userMapper.toDto(targetUser)).thenReturn(userResponseDto)

        // When
        val result = userService.getUserById(userPrincipal, targetUserId)

        // Then
        assertEquals(userResponseDto, result)
        verify(userRepository, times(2)).findById(targetUserId)
        verify(userRepository).findById(authenticatedUserId)
        verify(userMapper).toDto(targetUser)
    }

    @Test
    fun `getUserById should throw NotFoundException when target user not found`() {
        // Given
        val authenticatedUserId = UUID.randomUUID()
        val targetUserId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val userPrincipal =
            UserPrincipal(
                id = authenticatedUserId,
                companyId = companyId,
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(userRepository.findById(targetUserId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            userService.getUserById(userPrincipal, targetUserId)
        }
    }

    @Test
    fun `getCurrentUser should return current user details`() {
        // Given
        val userId = UUID.randomUUID()
        val user =
            UserEntity().apply {
                name = "Current User"
                email = "current@example.com"
                bio = "User bio"
            }
        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Current User",
                email = "current@example.com",
                bio = "User bio",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
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
        whenever(userMapper.toDto(user)).thenReturn(userResponseDto)

        // When
        val result = userService.getCurrentUser(userPrincipal)

        // Then
        assertEquals(userResponseDto, result)
        verify(userRepository).findById(userId)
        verify(userMapper).toDto(user)
    }

    @Test
    fun `getCurrentUser should throw NotFoundException when user not found`() {
        // Given
        val userId = UUID.randomUUID()
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            userService.getCurrentUser(userPrincipal)
        }
    }

    @Test
    fun `searchUsersForUserCompany should return users from same company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val searchRequest = UserSearchRequestDto(query = "John")
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val user =
            UserEntity().apply {
                name = "Auth User"
                email = "auth@example.com"
                this.company = company
            }
        val foundUser =
            UserEntity().apply {
                name = "John Doe"
                email = "john@example.com"
            }
        val userResponseDto =
            UserResponseDto(
                id = UUID.randomUUID(),
                name = "John Doe",
                email = "john@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
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

        whenever(userRepository.getReferenceById(userId)).thenReturn(user)
        whenever(userRepository.search(company, searchRequest.query)).thenReturn(listOf(foundUser))
        whenever(userMapper.toDto(foundUser)).thenReturn(userResponseDto)

        // When
        val result = userService.searchUsersForUserCompany(searchRequest, userPrincipal)

        // Then
        assertEquals(1, result.size)
        assertEquals(listOf(userResponseDto), result)
        verify(userRepository).getReferenceById(userId)
        verify(userRepository).search(company, searchRequest.query)
        verify(userMapper).toDto(foundUser)
    }

    @Test
    fun `updateUserCredentials should update user name and bio`() {
        // Given
        val userId = UUID.randomUUID()
        val updateDto =
            UserUpdateCredentialsDto(
                name = "Updated Name",
                bio = "Updated bio",
            )
        val user =
            UserEntity().apply {
                name = "Original Name"
                email = "user@example.com"
                bio = "Original bio"
            }
        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Updated Name",
                email = "user@example.com",
                bio = "Updated bio",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
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

        whenever(userRepository.getReferenceById(userId)).thenReturn(user)
        whenever(userMapper.toDto(user)).thenReturn(userResponseDto)

        // When
        val result = userService.updateUserCredentials(updateDto, userPrincipal)

        // Then
        assertEquals(userResponseDto, result)
        assertEquals("Updated Name", user.name)
        assertEquals("Updated bio", user.bio)
        verify(userRepository).getReferenceById(userId)
        verify(userMapper).toDto(user)
    }

    @Test
    fun `updateUserCredentials should keep existing values when null provided`() {
        // Given
        val userId = UUID.randomUUID()
        val updateDto =
            UserUpdateCredentialsDto(
                name = null,
                bio = "Updated bio",
            )
        val user =
            UserEntity().apply {
                name = "Original Name"
                email = "user@example.com"
                bio = "Original bio"
            }
        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Original Name",
                email = "user@example.com",
                bio = "Updated bio",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
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

        whenever(userRepository.getReferenceById(userId)).thenReturn(user)
        whenever(userMapper.toDto(user)).thenReturn(userResponseDto)

        // When
        val result = userService.updateUserCredentials(updateDto, userPrincipal)

        // Then
        assertEquals(userResponseDto, result)
        assertEquals("Original Name", user.name) // Should remain unchanged
        assertEquals("Updated bio", user.bio)
        verify(userRepository).getReferenceById(userId)
        verify(userMapper).toDto(user)
    }
}

