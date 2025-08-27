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

/**
 * TODO()
 *
 * @property companyRepository TODO()
 * @property companyMapper TODO()
 * @property userRepository TODO()
 */
@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val companyMapper: CompanyMapper,
    private val userRepository: UserRepository,
) {
    /**
     * TODO()
     *
     * @param request TODO()
     * @return TODO()
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

    /**
     * TODO()
     *
     * @param user TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getCompanyForUser(user: UserPrincipal): CompanyResponseDto {
        val company =
            companyRepository
                .findById(user.companyId)
                .orElseThrow { NotFoundException("Company not found for the current user.") }
        return companyMapper.toDto(company)
    }

    /**
     * TODO()
     *
     * @param id TODO()
     * @param request TODO()
     * @return TODO()
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
     * TODO()
     *
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getAllCompanies(): List<CompanyResponseDto> {
        val companies = companyRepository.findAll()
        return companies.map { companyMapper.toDto(it) }
    }

    /**
     * TODO()
     *
     * @param id TODO()
     * @return TODO()
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
     * TODO()
     *
     * @param searchTerm TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun searchCompanies(searchTerm: String): List<CompanyResponseDto> {
        if (searchTerm.isBlank()) {
            return getAllCompanies()
        }

        val matchedCompanies = companyRepository.findByNameContainingIgnoreCase(searchTerm)

        return matchedCompanies.map { companyMapper.toDto(it) }
    }

    /**
     * TODO()
     *
     * @param userId TODO()
     * @param companyId TODO()
     */
    @Transactional
    fun addUser(
        userId: UUID,
        companyId: UUID,
    ) {
        val company = companyRepository.getReferenceById(companyId)
        val user = userRepository.getReferenceById(userId)
        company.users.add(user)
    }

    /**
     * TODO()
     *
     * @param userId TODO()
     * @param companyId TODO()
     */
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
