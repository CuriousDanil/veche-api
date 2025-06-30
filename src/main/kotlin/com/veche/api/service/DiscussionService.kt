package com.veche.api.service

import com.veche.api.database.model.*
import com.veche.api.database.repository.*
import com.veche.api.dto.discussion.DiscussionRequestDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import com.veche.api.dto.discussion.DiscussionUpdateDto
import com.veche.api.event.ActionPayload
import com.veche.api.event.DiscussionResolvedEvent
import com.veche.api.event.PayloadMapper
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.DiscussionMapper
import com.veche.api.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class DiscussionService(
    private val discussionRepository: DiscussionRepository,
    private val partyRepository: PartyRepository,
    private val discussionVoteRepository: DiscussionVoteRepository,
    private val userRepository: UserRepository,
    private val discussionMapper: DiscussionMapper,
    private val publisher: ApplicationEventPublisher,
    private val actionRepository: PendingActionRepository,
    private val payloadMapper: PayloadMapper,
) {
    @Transactional(readOnly = true)
    fun getAllDiscussionsForUserCompany(user: UserPrincipal): List<DiscussionResponseDto> {
        val companyId = user.companyId

        val discussions = discussionRepository.findAllByPartyCompanyId(companyId)
        return discussions.map { discussionMapper.toDto(it) }
    }

    @Transactional
    fun createDiscussion(
        user: UserPrincipal,
        request: DiscussionRequestDto,
    ): DiscussionResponseDto {
        val party =
            partyRepository
                .findById(request.partyId)
                .orElseThrow { NotFoundException("Party not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User is not a member of the selected party.")
        }

        val discussion =
            DiscussionEntity().apply {
                subject = request.subject
                content = request.content
                fileUrl = request.fileUrl
                fileName = request.fileName
                fileSize = request.fileSize
                this.party = party
                creator = userEntity
            }

        return discussionMapper.toDto(discussionRepository.save(discussion))
    }

    @Transactional
    fun voteOnDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
        voteValue: VoteValue,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User is not a member of the discussion's party.")
        }

        if (discussion.status !in listOf(DiscussionStatus.VOTING, DiscussionStatus.FINAL_VOTING)) {
            throw ForbiddenException("Discussion is not in voting state.")
        }

        val vote =
            DiscussionVoteEntity().apply {
                this.discussion = discussion
                this.user = userEntity
                this.voteValue = voteValue
            }

        discussionVoteRepository.upsertVote(vote)
    }

    @Transactional
    fun updateDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
        updateDto: DiscussionUpdateDto,
    ): DiscussionResponseDto {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        if (discussion.creator.id != user.id) {
            throw ForbiddenException("Only the creator can update the discussion.")
        }

        if (discussion.status != DiscussionStatus.WAITING) {
            throw ForbiddenException("Only discussions in WAITING status can be updated.")
        }

        discussion.apply {
            subject = updateDto.subject ?: subject
            content = updateDto.content ?: content
            fileUrl = updateDto.fileUrl ?: fileUrl
            fileName = updateDto.fileName ?: fileName
            fileSize = updateDto.fileSize ?: fileSize
        }

        return discussionMapper.toDto(discussion)
    }

    @Transactional
    fun archiveDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        if (discussion.status != DiscussionStatus.WAITING || discussion.status != DiscussionStatus.RESOLVED) {
            throw ForbiddenException("Only discussions in WAITING or RESOLVED status can be archived.")
        }

        discussion.apply {
            status = DiscussionStatus.ARCHIVED
        }
    }

    @Transactional
    fun putDiscussionOnVoting(
        // TODO : Move management to sessions
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        if (discussion.status != DiscussionStatus.WAITING) {
            throw ForbiddenException("Only discussions in WAITING status can be put on voting.")
        }

        discussion.apply {
            status = DiscussionStatus.VOTING
        }
    }

    @Transactional
    fun putDiscussionOnFinalVoting(
        // TODO : Move management to sessions
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        /*if (discussion.status != DiscussionStatus.VOTING) {
            throw ForbiddenException("Only discussions in VOTING status can be put on final voting.")
        }*/

        discussion.apply {
            status = DiscussionStatus.FINAL_VOTING
        }
    }

    @Transactional
    fun resolveDiscussion(
        // TODO : Move management to sessions
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val log = LoggerFactory.getLogger(javaClass)
        log.debug("Resolving discussion for user {}", user.id)

        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        if (discussion.status != DiscussionStatus.FINAL_VOTING) {
            throw ForbiddenException("Only discussions in FINAL_VOTING status can be resolved.")
        }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        val approved =
            discussion.votes
                .groupBy { it.voteValue }
                .maxByOrNull { it.value.size }
                ?.key ?: VoteValue.DISAGREE

        log.info("Discussion {} resolved with vote {}", discussionId, approved)

        publisher.publishEvent(
            DiscussionResolvedEvent(
                discussionId = discussion.id,
                approved = approved == VoteValue.AGREE,
            ),
        )

        discussion.apply {
            status = DiscussionStatus.RESOLVED
        }
    }

    @Transactional
    fun putDiscussionOnWait(
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (!userEntity.isAbleToManageSessions) {
            throw ForbiddenException("User is not authorized to manage sessions.")
        }

        if (discussion.status != DiscussionStatus.ARCHIVED || discussion.status != DiscussionStatus.RESOLVED) {
            throw ForbiddenException("Only discussions in ARCHIVED or RESOLVED status can be put on waiting.")
        }

        discussion.apply {
            status = DiscussionStatus.WAITING
        }
    }

    @Transactional
    fun deleteDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.creator.id != userEntity.id) {
            throw ForbiddenException("Only the creator can delete the discussion.")
        }

        discussion.apply {
            deletedAt = Instant.now()
        }
    }

    @Transactional
    fun addActionToDiscussion(
        user: UserPrincipal,
        discussionId: UUID,
        action: ActionPayload,
    ) {
        val discussion =
            discussionRepository
                .findById(discussionId)
                .orElseThrow { NotFoundException("Discussion not found.") }

        val userEntity =
            userRepository
                .findById(user.id)
                .orElseThrow { NotFoundException("Authenticated user not found.") }

        if (discussion.party.users.none { it.id == userEntity.id }) {
            throw ForbiddenException("User does not belong to the discussion's party.")
        }

        if (discussion.status != DiscussionStatus.WAITING) {
            throw ForbiddenException("Only discussions in WAITING status can have actions added.")
        }

        actionRepository.save(
            PendingActionEntity().apply {
                this.discussion = discussion
                this.actionType = action.type
                this.payload = payloadMapper.toJson(action)
                this.executed = false
            },
        )
    }
}
