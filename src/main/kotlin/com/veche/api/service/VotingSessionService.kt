package com.veche.api.service

import com.veche.api.database.model.VotingSessionEntity
import com.veche.api.database.repository.DiscussionRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.VotingSessionRepository
import com.veche.api.dto.votingSession.VotingSessionRequestDto
import com.veche.api.dto.votingSession.VotingSessionResponseDto
import com.veche.api.dto.votingSession.VotingSessionUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.VotingSessionMapper
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VotingSessionService(
    private val votingSessionRepository: VotingSessionRepository,
    private val discussionRepository: DiscussionRepository,
    private val partyRepository: PartyRepository,
    private val votingSessionMapper: VotingSessionMapper,
) {
    @Transactional(readOnly = true)
    fun getAllVotingSessions(user: UserPrincipal): List<VotingSessionResponseDto> {
        val companyId = user.companyId
        val votingSessions = votingSessionRepository.findAllByPartyCompanyId(companyId)
        return votingSessions.map { votingSessionMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    fun getVotingSessionById(
        user: UserPrincipal,
        id: UUID,
    ): VotingSessionResponseDto {
        val votingSession =
            votingSessionRepository
                .findById(id)
                .orElseThrow { NotFoundException("Voting session not found.") }
        return votingSessionMapper.toDto(votingSession)
    }

    @Transactional
    fun createVotingSession(
        user: UserPrincipal,
        request: VotingSessionRequestDto,
    ): VotingSessionResponseDto {
        val party =
            partyRepository
                .findById(request.partyId)
                .orElseThrow { NotFoundException("Party not found.") }
        val discussions = discussionRepository.findAllById(request.discussionIds)
        if (discussions.size != request.discussionIds.size) {
            throw NotFoundException("One or more discussions not found.")
        }
        val votingSession =
            VotingSessionEntity().apply {
                this.name = request.name
                this.party = party
                this.discussions = discussions.toMutableSet()
                this.firstRoundStartsAt = request.firstRoundStartsAt
                this.secondRoundStartsAt = request.secondRoundStartsAt
                this.endsAt = request.endsAt
            }
        val savedSession = votingSessionRepository.save(votingSession)
        discussions.forEach {
            it.apply {
                session = savedSession
            }
        }
        return votingSessionMapper.toDto(savedSession)
    }

    @Transactional
    fun updateVotingSession(
        user: UserPrincipal,
        id: UUID,
        updateDto: VotingSessionUpdateDto,
    ): VotingSessionResponseDto {
        val votingSession =
            votingSessionRepository
                .findById(id)
                .orElseThrow { NotFoundException("Voting session not found.") }

        updateDto.name?.let { votingSession.name = it }
        updateDto.partyId?.let {
            val party =
                partyRepository
                    .findById(it)
                    .orElseThrow { NotFoundException("Party not found.") }
            votingSession.party = party
        }
        updateDto.firstRoundStart?.let { votingSession.firstRoundStartsAt = it }
        updateDto.secondRoundStart?.let { votingSession.secondRoundStartsAt = it }
        updateDto.endTime?.let { votingSession.endsAt = it }
        updateDto.discussionIds?.let { it ->
            val discussions = discussionRepository.findAllById(it).toList()
            if (discussions.size != it.size) {
                throw NotFoundException("One or more discussions not found.")
            }
            votingSession.discussions.forEach { discussion ->
                discussion.apply {
                    session = null
                }
            }
            votingSession.discussions = discussions.toMutableSet()
            votingSession.discussions.forEach { discussion ->
                discussion.apply {
                    session = votingSession
                }
            }
        }

        return votingSessionMapper.toDto(votingSession)
    }
}
