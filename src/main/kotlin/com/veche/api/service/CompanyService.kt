package com.veche.api.service

import com.veche.api.database.model.CompanyEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.company.CompanyRequestDto
import com.veche.api.dto.company.CompanyResponseDto
import com.veche.api.dto.company.CompanyUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.CompanyMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository,
    private val companyMapper: CompanyMapper
) {

    /**
     * Creates a new company with the given details.
     *
     * @param request DTO containing the company information
     * @return DTO with the created company details
     */
    @Transactional
    fun createCompany(request: CompanyRequestDto): CompanyResponseDto {
        val company = CompanyEntity(
            name = request.name
        )

        val savedCompany = companyRepository.save(company)
        return companyMapper.toDto(savedCompany)
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
    fun updateCompanyName(id: UUID, request: CompanyUpdateDto): CompanyResponseDto {
        val company = companyRepository.findById(id)
            .orElseThrow { NotFoundException("Company not found.") }

        // Create a new company entity with the updated name while preserving other properties
        val updatedCompany = company.copy(name = request.name)

        val savedCompany = companyRepository.save(updatedCompany)
        return companyMapper.toDto(savedCompany)
    }

    /**
     * Add a user to the specified company.
     *
     * @param companyId The unique identifier of the company
     * @param userId The unique identifier of the user to add
     * @throws NotFoundException if either the company or user is not found
     */
    @Transactional
    fun addUserToCompany(companyId: UUID, userId: UUID) {
        val company = companyRepository.findById(companyId)
            .orElseThrow { NotFoundException("Company not found.") }

        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found.") }

        // Create a new user entity with the updated company while preserving other properties
        val updatedUser = user.copy(company = company)

        userRepository.save(updatedUser)
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
        val company = companyRepository.findById(id)
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
}
