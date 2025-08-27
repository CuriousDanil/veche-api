package com.veche.api.service

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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * TODO()
 *
 * @property companyRepository TODO()
 * @property partyRepository TODO()
 * @property partyMapper TODO()
 * @property userRepository TODO()
 * @property log TODO()
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
     * TODO()
     *
     * @param request TODO()
     * @param user TODO()
     * @return TODO()
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
     * TODO()
     *
     * @param request TODO()
     * @param partyId TODO()
     * @return TODO()
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
     * TODO()
     *
     * @param user TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getAllPartiesForUserCompany(user: UserPrincipal): List<PartyResponseDto> = getAllPartiesForCompany(user.companyId)

    /**
     * TODO()
     *
     * @param partyId TODO()
     * @param user TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getPartyById(
        partyId: UUID,
        user: UserPrincipal,
    ): PartyResponseDto {
        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }
        val partyEntity =
            partyRepository
                .findById(partyId)
                .orElseThrow { NotFoundException("Party not found.") }
        if (userEntity.company != partyEntity.company) {
            throw { ForbiddenException("User does not belong to the party's company") } as Throwable
        }

        return partyMapper.toDto(partyEntity)
    }

    /**
     * TODO()
     *
     * @param user TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getPartiesForUser(user: UserPrincipal): List<PartyResponseDto> = partyRepository.findAllById(user.partyIds).map(partyMapper::toDto)

    /**
     * TODO()
     *
     * @param companyId TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getAllPartiesForCompany(companyId: UUID): List<PartyResponseDto> =
        partyRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId).map(partyMapper::toDto)

    /**
     * TODO()
     *
     * @param partyId TODO()
     */
    @Transactional
    fun deleteParty(partyId: UUID) = findPartyById(partyId).also { it.deletedAt = Instant.now() }

    /**
     * TODO()
     *
     * @param partyId TODO()
     * @param userId TODO()
     */
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

    /**
     * TODO()
     *
     * @param user TODO()
     * @param party TODO()
     */
    private fun ensureCompanyMembership(
        user: UserEntity,
        party: PartyEntity,
    ) {
        if (user.company?.id != party.company.id) {
            user.company = party.company
            party.company.users.add(user)
        }
    }

    /**
     * TODO()
     *
     * @param partyId TODO()
     * @param userId TODO()
     */
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

    /**
     * TODO()
     *
     * @param partyId TODO()
     * @return TODO()
     */
    private fun findPartyById(partyId: UUID) =
        partyRepository
            .findById(partyId)
            .orElseThrow { NotFoundException("Party not found") }
}
