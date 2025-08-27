package com.veche.api.service

import com.veche.api.database.model.*
import com.veche.api.database.repository.*
import com.veche.api.dto.discussion.DiscussionRequestDto
import com.veche.api.dto.discussion.DiscussionResponseDto
import com.veche.api.dto.discussion.DiscussionUpdateDto
import com.veche.api.dto.party.PartyResponseDto
import com.veche.api.event.PayloadMapper
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.DiscussionMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant
import java.util.*

class DiscussionServiceTest {
    private lateinit var discussionService: DiscussionService
    private lateinit var discussionRepository: DiscussionRepository
    private lateinit var partyRepository: PartyRepository
    private lateinit var discussionVoteRepository: DiscussionVoteRepository
    private lateinit var userRepository: UserRepository
    private lateinit var discussionMapper: DiscussionMapper
    private lateinit var publisher: ApplicationEventPublisher
    private lateinit var actionRepository: PendingActionRepository
    private lateinit var payloadMapper: PayloadMapper

    @BeforeEach
    fun setUp() {
        discussionRepository = mock()
        partyRepository = mock()
        discussionVoteRepository = mock()
        userRepository = mock()
        discussionMapper = mock()
        publisher = mock()
        actionRepository = mock()
        payloadMapper = mock()

        discussionService =
            DiscussionService(
                discussionRepository,
                partyRepository,
                discussionVoteRepository,
                userRepository,
                discussionMapper,
                publisher,
                actionRepository,
                payloadMapper,
            )
    }

    @Test
    fun `getAllDiscussionsForUserCompany should return discussions for user's company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val discussion1 =
            DiscussionEntity().apply {
                subject = "Discussion 1"
                content = "Content 1"
                status = DiscussionStatus.WAITING
            }
        val discussion2 =
            DiscussionEntity().apply {
                subject = "Discussion 2"
                content = "Content 2"
                status = DiscussionStatus.VOTING
            }
        val discussions = listOf(discussion1, discussion2)
        val discussionResponses =
            listOf(
                DiscussionResponseDto(
                    id = UUID.randomUUID(),
                    subject = "Discussion 1",
                    content = "Content 1",
                    createdAt = Instant.now(),
                    party = PartyResponseDto(UUID.randomUUID(), "Test Party"),
                    creatorName = "Test User",
                    status = DiscussionStatus.WAITING,
                    votes = listOf(),
                    actions = listOf(),
                ),
                DiscussionResponseDto(
                    id = UUID.randomUUID(),
                    subject = "Discussion 2",
                    content = "Content 2",
                    createdAt = Instant.now(),
                    party = PartyResponseDto(UUID.randomUUID(), "Test Party"),
                    creatorName = "Test User",
                    status = DiscussionStatus.VOTING,
                    votes = listOf(),
                    actions = listOf(),
                ),
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

        whenever(discussionRepository.findAllByPartyCompanyId(companyId)).thenReturn(discussions)
        whenever(discussionMapper.toDto(discussion1)).thenReturn(discussionResponses[0])
        whenever(discussionMapper.toDto(discussion2)).thenReturn(discussionResponses[1])

        // When
        val result = discussionService.getAllDiscussionsForUserCompany(userPrincipal)

        // Then
        assertEquals(2, result.size)
        assertEquals(discussionResponses, result)
        verify(discussionRepository).findAllByPartyCompanyId(companyId)
        verify(discussionMapper, times(2)).toDto(any<DiscussionEntity>())
    }

    @Test
    fun `createDiscussion should create and return discussion successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val discussionRequest =
            DiscussionRequestDto(
                subject = "Test Discussion",
                content = "Test content",
                partyId = partyId,
            )
        val party =
            PartyEntity().apply {
                name = "Test Party"
            }
        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }
        val savedDiscussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                content = "Test content"
                this.party = party
                creator = user
                status = DiscussionStatus.WAITING
            }
        val discussionResponse =
            DiscussionResponseDto(
                id = UUID.randomUUID(),
                subject = "Test Discussion",
                content = "Test content",
                createdAt = Instant.now(),
                party = PartyResponseDto(partyId, "Test Party"),
                creatorName = "Test User",
                status = DiscussionStatus.WAITING,
                votes = listOf(),
                actions = listOf(),
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(partyId),
                isAbleToPostDiscussions = true,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(discussionRepository.save(any<DiscussionEntity>())).thenReturn(savedDiscussion)
        whenever(discussionMapper.toDto(savedDiscussion)).thenReturn(discussionResponse)

        // When
        val result = discussionService.createDiscussion(userPrincipal, discussionRequest)

        // Then
        assertEquals(discussionResponse, result)
        verify(partyRepository).findById(partyId)
        verify(userRepository).findById(userId)
        verify(discussionRepository).save(any<DiscussionEntity>())
        verify(discussionMapper).toDto(savedDiscussion)
    }

    @Test
    fun `createDiscussion should throw NotFoundException when party not found`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val discussionRequest =
            DiscussionRequestDto(
                subject = "Test Discussion",
                content = "Test content",
                partyId = partyId,
            )
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(partyId),
                isAbleToPostDiscussions = true,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            discussionService.createDiscussion(userPrincipal, discussionRequest)
        }
    }

    @Test
    fun `createDiscussion should throw ForbiddenException when user cannot post discussions`() {
        // Given
        val userId = UUID.randomUUID()
        val partyId = UUID.randomUUID()
        val discussionRequest =
            DiscussionRequestDto(
                subject = "Test Discussion",
                content = "Test content",
                partyId = partyId,
            )
        val party =
            PartyEntity().apply {
                name = "Test Party"
            }
        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(partyId),
                isAbleToPostDiscussions = false, // User cannot post discussions
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(partyRepository.findById(partyId)).thenReturn(Optional.of(party))

        // When/Then
        assertThrows<ForbiddenException> {
            discussionService.createDiscussion(userPrincipal, discussionRequest)
        }
    }
}

