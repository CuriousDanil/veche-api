package com.veche.api.service

import com.veche.api.database.model.*
import com.veche.api.database.repository.*
import com.veche.api.dto.discussion.DiscussionRequestDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import com.veche.api.dto.discussion.DiscussionUpdateDto
import com.veche.api.mapper.DiscussionMapper
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DiscussionService(
    private val discussionRepository: DiscussionRepository,
    private val partyRepository: PartyRepository,
    private val discussionVoteRepository: DiscussionVoteRepository,
    private val userRepository: UserRepository,
    private val discussionMapper: DiscussionMapper
) {

    @Transactional(readOnly = true)
    fun getAllDiscussionsForUserCompany(user: UserPrincipal): List<DiscussionResponseDto> {
        val companyId = user.companyId

        val discussions = discussionRepository.findAllByPartyCompanyId(companyId)
        return discussions.map { discussionMapper.toDto(it) }
    }

    @Transactional
    fun createDiscussion(user: UserPrincipal, request: DiscussionRequestDto): DiscussionResponseDto {
        val party = partyRepository.findById(request.partyId)
            .orElseThrow { NotFoundException("Party not found.") }

        val userEntity = userRepository.findById(user.id)
            .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User is not a member of the selected party.")
        }

        val discussion = DiscussionEntity(
            subject = request.subject,
            content = request.content,
            fileUrl = request.fileUrl,
            fileName = request.fileName,
            fileSize = request.fileSize,
            party = party,
            creator = userEntity
        )

        return discussionMapper.toDto(discussionRepository.save(discussion))
    }

    @Transactional
    fun voteOnDiscussion(user: UserPrincipal, discussionId: UUID, voteValue: VoteValue) {
        val discussion = discussionRepository.findById(discussionId)
            .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity = userRepository.findById(user.id)
            .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User is not a member of the discussion's party.")
        }

        val vote = DiscussionVoteEntity(
            discussion = discussion,
            user = userEntity,
            voteValue = voteValue
        )

        discussionVoteRepository.save(vote)
    }

    @Transactional
    fun updateDiscussion(user: UserPrincipal, discussionId: UUID, updateDto: DiscussionUpdateDto): DiscussionResponseDto {
        val discussion = discussionRepository.findById(discussionId)
            .orElseThrow { NotFoundException("Discussion not found.") }

        if (discussion.creator.id != user.id) {
            throw ForbiddenException("Only the creator can update the discussion.")
        }

        if (discussion.status != DiscussionStatus.WAITING) {
            throw ForbiddenException("Only discussions in WAITING status can be updated.")
        }

        val updatedDiscussion = discussion.copy(
            subject = updateDto.subject ?: discussion.subject,
            fileUrl = updateDto.fileUrl ?: discussion.fileUrl,
            fileName = updateDto.fileName ?: discussion.fileName,
            fileSize = updateDto.fileSize ?: discussion.fileSize
        )

        return discussionMapper.toDto(discussionRepository.save(updatedDiscussion))
    }

    @Transactional
    fun archiveDiscussion(user: UserPrincipal, discussionId: UUID) {
        val discussion = discussionRepository.findById(discussionId)
            .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity = userRepository.findById(user.id)
            .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        if (discussion.status != DiscussionStatus.WAITING) {
            throw ForbiddenException("Only discussions in WAITING status can be archived.")
        }

        discussionRepository.save(discussion.copy(status = DiscussionStatus.ARCHIVED))
    }

    @Transactional
    fun putDiscussionOnWait(user: UserPrincipal, discussionId: UUID) {
        val discussion = discussionRepository.findById(discussionId)
            .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity = userRepository.findById(user.id)
            .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        if (discussion.status != DiscussionStatus.ARCHIVED) {
            throw ForbiddenException("Only discussions in ARCHIVED status can be put on waiting.")
        }

        discussionRepository.save(discussion.copy(status = DiscussionStatus.WAITING))
    }
}