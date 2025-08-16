package com.veche.api.service

import com.veche.api.database.model.PartyEntity
import com.veche.api.database.model.UserEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.party.PartyRequestDto
import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.dto.party.PartyUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.PartyMapper
import com.veche.api.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * Service layer for managing party operations within the Veche application.
 *
 * This service provides comprehensive CRUD operations for parties, including creation,
 * updates, retrieval, and deletion. It handles party-company relationships and ensures
 * proper data access based on user permissions and company associations.
 *
 * @property companyRepository Repository for company data access operations
 * @property partyRepository Repository for party data access operations
 * @property partyMapper Mapper for converting between entity and DTO representations
 *
 */
@Service
class PartyService(
    private val companyRepository: CompanyRepository,
    private val partyRepository: PartyRepository,
    private val partyMapper: PartyMapper,
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Creates a new party within a specified company.
     *
     * This method validates that the specified company exists before creating the party.
     * The operation is performed within a database transaction to ensure data consistency.
     *
     * @param request The party creation request containing party details and company ID
     * @return PartyResponseDto containing the created party information
     * @throws NotFoundException if the specified company does not exist
     */
    @Transactional
    fun createParty(
        request: PartyRequestDto,
        user: UserPrincipal,
    ): PartyResponseDto =
        partyMapper.toDto(
            partyRepository.save(
                PartyEntity().apply {
                    name = request.name
                    this.company = companyRepository.getReferenceById(user.companyId)
                    users = mutableSetOf(userRepository.getReferenceById(user.id))
                },
            ),
        )

    /**
     * Updates an existing party's information.
     *
     * This method retrieves the existing party, updates its properties based on the request,
     * and saves the changes to the database within a transaction.
     *
     * @param request The update request containing new party information
     * @param partyId The unique identifier of the party to update
     * @return PartyResponseDto containing the updated party information
     * @throws NotFoundException if the specified party does not exist
     */
    @Transactional
    fun updateParty(
        request: PartyUpdateDto,
        partyId: UUID,
    ): PartyResponseDto {
        val party = findPartyById(partyId)
        party.name = request.name
        // No explicit save needed due to @Transactional context
        return partyMapper.toDto(party)
    }

    /**
     * Retrieves all parties belonging to the user's company.
     *
     * This is a convenience method that delegates to [getAllPartiesForCompany] using
     * the company ID from the user's principal. The operation is read-only.
     *
     * @param user The authenticated user principal containing company information
     * @return List of PartyResponseDto representing all parties in the user's company
     */
    @Transactional(readOnly = true)
    fun getAllPartiesForUserCompany(user: UserPrincipal): List<PartyResponseDto> = getAllPartiesForCompany(user.companyId)

    /**
     * Retrieves all parties that the specified user has access to.
     *
     * This method returns only the parties that are explicitly associated with the user
     * through their party IDs list. The operation is read-only.
     *
     * @param user The authenticated user principal containing accessible party IDs
     * @return List of PartyResponseDto representing parties accessible to the user
     */
    @Transactional(readOnly = true)
    fun getPartiesForUser(user: UserPrincipal): List<PartyResponseDto> = partyRepository.findAllById(user.partyIds).map(partyMapper::toDto)

    /**
     * Retrieves all parties belonging to a specific company.
     *
     * This method fetches all parties associated with the given company ID.
     * The operation is read-only and returns an empty list if no parties are found.
     *
     * @param companyId The unique identifier of the company
     * @return List of PartyResponseDto representing all parties in the specified company
     */
    @Transactional(readOnly = true)
    fun getAllPartiesForCompany(companyId: UUID): List<PartyResponseDto> =
        partyRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId).map(partyMapper::toDto)

    /**
     * Deletes a party from the system.
     *
     * This method first verifies that the party exists before attempting deletion.
     * The operation is performed within a database transaction to ensure data consistency.
     * Related data and associations should be handled by database cascading rules.
     *
     * @param partyId The unique identifier of the party to delete
     * @throws NotFoundException if the specified party does not exist
     */
    @Transactional
    fun deleteParty(partyId: UUID) = findPartyById(partyId).also { it.deletedAt = Instant.now() }

    @Transactional
    fun addUserToParty(
        partyId: UUID,
        userId: UUID,
    ) {
        if (partyRepository.existsByIdAndUsersId(partyId, userId)) {
            log.warn("User $userId is already a member of party $partyId. Action is idempotent.")
            return
        }

        val party = findPartyById(partyId)
        val user = userRepository.findById(userId).orElseThrow { NotFoundException("User not found") }

        ensureCompanyMembership(user, party)

        party.users.add(user)
        user.parties.add(party)
    }

    private fun ensureCompanyMembership(
        user: UserEntity,
        party: PartyEntity,
    ) {
        if (user.company?.id != party.company.id) {
            user.company = party.company
            party.company.users.add(user)
        }
    }

    @Transactional
    fun evictUserFromParty(
        partyId: UUID,
        userId: UUID,
    ) {
        if (!partyRepository.existsByIdAndUsersId(partyId, userId)) {
            log.warn("User $userId is not a member of party $partyId. Action is idempotent.")
            return
        }

        val party = findPartyById(partyId)
        val user = userRepository.getReferenceById(userId)

        party.users.remove(user)
        user.parties.remove(party)
    }

    private fun findPartyById(partyId: UUID) =
        partyRepository
            .findById(partyId)
            .orElseThrow { NotFoundException("Party not found") }
}
