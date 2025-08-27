package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.company.CompanyRequestDto
import com.veche.api.dto.company.CompanyResponseDto
import com.veche.api.dto.company.CompanyUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.CompanyMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*

class CompanyServiceTest {
    private lateinit var companyService: CompanyService
    private lateinit var companyRepository: CompanyRepository
    private lateinit var companyMapper: CompanyMapper
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        companyRepository = mock()
        companyMapper = mock()
        userRepository = mock()
        companyService = CompanyService(companyRepository, companyMapper, userRepository)
    }

    @Test
    fun `createCompany should create and return company successfully`() {
        // Given
        val companyRequest = CompanyRequestDto(name = "Test Company")
        val savedCompany =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val companyResponse =
            CompanyResponseDto(
                id = UUID.randomUUID(),
                name = "Test Company",
                users = listOf(),
                parties = listOf(),
            )

        whenever(companyRepository.save(any<CompanyEntity>())).thenReturn(savedCompany)
        whenever(companyMapper.toDto(savedCompany)).thenReturn(companyResponse)

        // When
        val result = companyService.createCompany(companyRequest)

        // Then
        assertEquals(companyResponse, result)
        verify(companyRepository).save(any<CompanyEntity>())
        verify(companyMapper).toDto(savedCompany)
    }

    @Test
    fun `getCompanyForUser should return user's company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val company =
            CompanyEntity().apply {
                name = "User Company"
            }
        val companyResponse =
            CompanyResponseDto(
                id = companyId,
                name = "User Company",
                users = listOf(),
                parties = listOf(),
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

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.of(company))
        whenever(companyMapper.toDto(company)).thenReturn(companyResponse)

        // When
        val result = companyService.getCompanyForUser(userPrincipal)

        // Then
        assertEquals(companyResponse, result)
        verify(companyRepository).findById(companyId)
        verify(companyMapper).toDto(company)
    }

    @Test
    fun `getCompanyForUser should throw NotFoundException when company not found`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = companyId,
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            companyService.getCompanyForUser(userPrincipal)
        }
    }

    @Test
    fun `updateCompanyName should update and return company`() {
        // Given
        val companyId = UUID.randomUUID()
        val updateDto = CompanyUpdateDto(name = "Updated Company")
        val company =
            CompanyEntity().apply {
                name = "Original Company"
            }
        val companyResponse =
            CompanyResponseDto(
                id = companyId,
                name = "Updated Company",
                users = listOf(),
                parties = listOf(),
            )

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.of(company))
        whenever(companyMapper.toDto(company)).thenReturn(companyResponse)

        // When
        val result = companyService.updateCompanyName(companyId, updateDto)

        // Then
        assertEquals(companyResponse, result)
        assertEquals("Updated Company", company.name)
        verify(companyRepository).findById(companyId)
        verify(companyMapper).toDto(company)
    }

    @Test
    fun `updateCompanyName should throw NotFoundException when company not found`() {
        // Given
        val companyId = UUID.randomUUID()
        val updateDto = CompanyUpdateDto(name = "Updated Company")

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            companyService.updateCompanyName(companyId, updateDto)
        }
    }

    @Test
    fun `getAllCompanies should return all companies`() {
        // Given
        val company1 = CompanyEntity().apply { name = "Company 1" }
        val company2 = CompanyEntity().apply { name = "Company 2" }
        val companies = listOf(company1, company2)
        val companyResponses =
            listOf(
                CompanyResponseDto(UUID.randomUUID(), "Company 1", listOf(), listOf()),
                CompanyResponseDto(UUID.randomUUID(), "Company 2", listOf(), listOf()),
            )

        whenever(companyRepository.findAll()).thenReturn(companies)
        whenever(companyMapper.toDto(company1)).thenReturn(companyResponses[0])
        whenever(companyMapper.toDto(company2)).thenReturn(companyResponses[1])

        // When
        val result = companyService.getAllCompanies()

        // Then
        assertEquals(2, result.size)
        assertEquals(companyResponses, result)
        verify(companyRepository).findAll()
        verify(companyMapper, times(2)).toDto(any<CompanyEntity>())
    }

    @Test
    fun `getCompanyById should return company when found`() {
        // Given
        val companyId = UUID.randomUUID()
        val company =
            CompanyEntity().apply {
                name = "Test Company"
            }
        val companyResponse =
            CompanyResponseDto(
                id = companyId,
                name = "Test Company",
                users = listOf(),
                parties = listOf(),
            )

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.of(company))
        whenever(companyMapper.toDto(company)).thenReturn(companyResponse)

        // When
        val result = companyService.getCompanyById(companyId)

        // Then
        assertEquals(companyResponse, result)
        verify(companyRepository).findById(companyId)
        verify(companyMapper).toDto(company)
    }

    @Test
    fun `getCompanyById should throw NotFoundException when company not found`() {
        // Given
        val companyId = UUID.randomUUID()

        whenever(companyRepository.findById(companyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            companyService.getCompanyById(companyId)
        }
    }

    @Test
    fun `searchCompanies should return matching companies`() {
        // Given
        val searchTerm = "Test"
        val company1 = CompanyEntity().apply { name = "Test Company 1" }
        val company2 = CompanyEntity().apply { name = "Test Company 2" }
        val companies = listOf(company1, company2)
        val companyResponses =
            listOf(
                CompanyResponseDto(UUID.randomUUID(), "Test Company 1", listOf(), listOf()),
                CompanyResponseDto(UUID.randomUUID(), "Test Company 2", listOf(), listOf()),
            )

        whenever(companyRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(companies)
        whenever(companyMapper.toDto(company1)).thenReturn(companyResponses[0])
        whenever(companyMapper.toDto(company2)).thenReturn(companyResponses[1])

        // When
        val result = companyService.searchCompanies(searchTerm)

        // Then
        assertEquals(2, result.size)
        assertEquals(companyResponses, result)
        verify(companyRepository).findByNameContainingIgnoreCase(searchTerm)
        verify(companyMapper, times(2)).toDto(any<CompanyEntity>())
    }

    @Test
    fun `searchCompanies should return all companies when search term is blank`() {
        // Given
        val searchTerm = "  "
        val companies = listOf(CompanyEntity().apply { name = "Company 1" })
        val companyResponses = listOf(CompanyResponseDto(UUID.randomUUID(), "Company 1", listOf(), listOf()))

        whenever(companyRepository.findAll()).thenReturn(companies)
        whenever(companyMapper.toDto(any<CompanyEntity>())).thenReturn(companyResponses[0])

        // When
        val result = companyService.searchCompanies(searchTerm)

        // Then
        assertEquals(1, result.size)
        verify(companyRepository).findAll()
        verify(companyRepository, never()).findByNameContainingIgnoreCase(any())
    }

    @Test
    fun `addUser should add user to company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val company = CompanyEntity().apply { users = mutableSetOf() }
        val user = UserEntity()

        whenever(companyRepository.getReferenceById(companyId)).thenReturn(company)
        whenever(userRepository.getReferenceById(userId)).thenReturn(user)

        // When
        companyService.addUser(userId, companyId)

        // Then
        assertTrue(company.users.contains(user))
        verify(companyRepository).getReferenceById(companyId)
        verify(userRepository).getReferenceById(userId)
    }

    @Test
    fun `evictUser should remove user from company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val user = UserEntity()
        val company = CompanyEntity().apply { users = mutableSetOf(user) }

        whenever(companyRepository.getReferenceById(companyId)).thenReturn(company)
        whenever(userRepository.getReferenceById(userId)).thenReturn(user)

        // When
        companyService.evictUser(userId, companyId)

        // Then
        assertFalse(company.users.contains(user))
        verify(companyRepository).getReferenceById(companyId)
        verify(userRepository).getReferenceById(userId)
    }
}

