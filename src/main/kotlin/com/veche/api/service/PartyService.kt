package com.veche.api.service

import com.veche.api.database.model.PartyEntity
import com.veche.api.database.repository.CompanyRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.dto.party.PartyRequestDto
import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.dto.party.PartyUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.PartyMapper
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PartyService(
    private val companyRepository: CompanyRepository,
    private val partyRepository: PartyRepository,
    private val partyMapper: PartyMapper,
) {

    @Transactional
    fun createParty(request: PartyRequestDto): PartyResponseDto {

        val party = partyRepository.save(
            PartyEntity(
                name = request.name,
                company = companyRepository.findById(request.companyId)
                    .orElseThrow { NotFoundException("Company not found") }
            )
        )

        return partyMapper.toDto(party)
    }

    @Transactional
    fun updateParty(request: PartyUpdateDto, partyId: UUID): PartyResponseDto {

        val party = partyRepository.findById(partyId)
            .orElseThrow { NotFoundException("Party not found") }

        val updatedParty = party.copy(name = request.name)

        val savedParty = partyRepository.save(updatedParty)
        return partyMapper.toDto(savedParty)
    }

    @Transactional(readOnly = true)
    fun getAllPartiesForUserCompany(user: UserPrincipal): List<PartyResponseDto> {
        val companyId = user.companyId

        val parties = partyRepository.findAllByCompanyId(companyId)
        return parties.map { partyMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun getAllPartiesForCompany(companyId: UUID): List<PartyResponseDto> {
        val parties = partyRepository.findAllByCompanyId(companyId)
        return parties.map { partyMapper.toDto(it) }
    }

    @Transactional
    fun deleteParty(partyId: UUID) {
        val party = partyRepository.findById(partyId)
            .orElseThrow { NotFoundException("Party not found") }

        partyRepository.delete(party)
    }

}