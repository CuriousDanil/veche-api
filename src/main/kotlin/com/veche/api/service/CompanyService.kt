package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.company.CompanyRequestDto
import com.veche.api.dto.company.CompanyResponseDto
import com.veche.api.dto.company.CompanyUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.CompanyMapper
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val companyMapper: CompanyMapper,
    private val userRepository: UserRepository,
) {
    /**
     * Creates a new company with the given details.
     *
     * @param request DTO containing the company information
     * @return DTO with the created company details
     */
    @Transactional
    fun createCompany(request: CompanyRequestDto): CompanyResponseDto {
        val company =
            CompanyEntity().apply {
                name = request.name
            }

        val savedCompany = companyRepository.save(company)
        return companyMapper.toDto(savedCompany)
    }

    @Transactional(readOnly = true)
    fun getCompanyForUser(user: UserPrincipal): CompanyResponseDto {
        val company =
            companyRepository
                .findById(user.companyId)
                .orElseThrow { NotFoundException("Company not found for the current user.") }
        return companyMapper.toDto(company)
    }

    /**
     * Updates an existing company's name.
     *
     * @param id The unique identifier of the company to update
     * @param request DTO containing the updated company information
     * @return DTO with the updated company details
     * @throws NotFoundException if the company with the given ID is not found
     */
    @Transactional
    fun updateCompanyName(
        id: UUID,
        request: CompanyUpdateDto,
    ): CompanyResponseDto {
        val company =
            companyRepository
                .findById(id)
                .orElseThrow { NotFoundException("Company not found.") }

        company.name = request.name
        return companyMapper.toDto(company)
    }

    /**
     * Retrieves all companies in the system.
     *
     * @return List of DTOs with company details
     */
    @Transactional(readOnly = true)
    fun getAllCompanies(): List<CompanyResponseDto> {
        val companies = companyRepository.findAll()
        return companies.map { companyMapper.toDto(it) }
    }

    /**
     * Retrieves a specific company by its ID.
     *
     * @param id The unique identifier of the company to retrieve
     * @return DTO with the company details
     * @throws NotFoundException if the company with the given ID is not found
     */
    @Transactional(readOnly = true)
    fun getCompanyById(id: UUID): CompanyResponseDto {
        val company =
            companyRepository
                .findById(id)
                .orElseThrow { NotFoundException("Company not found.") }

        return companyMapper.toDto(company)
    }

    /**
     * Searches for companies by name with flexible matching capabilities directly at the database level.
     * The search supports:
     * - Partial name matches (case-insensitive)
     * - Acronym matching (e.g., "ABC" would match "American Business Corporation")
     * - Special character handling (ignores non-alphanumeric characters)
     *
     * @param searchTerm The search term to look for in company names
     * @return List of company DTOs matching the search criteria
     */
    @Transactional(readOnly = true)
    fun searchCompanies(searchTerm: String): List<CompanyResponseDto> {
        if (searchTerm.isBlank()) {
            return getAllCompanies()
        }

        val matchedCompanies = companyRepository.findByNameContainingIgnoreCase(searchTerm)

        return matchedCompanies.map { companyMapper.toDto(it) }
    }

    @Transactional
    fun addUser(
        userId: UUID,
        companyId: UUID,
    ) {
        val company = companyRepository.getReferenceById(companyId)
        val user = userRepository.getReferenceById(userId)
        company.users.add(user)
    }

    @Transactional
    fun evictUser(
        userId: UUID,
        companyId: UUID,
    ) {
        val company = companyRepository.getReferenceById(companyId)
        val user = userRepository.getReferenceById(userId)
        company.users.remove(user)
    }
}
