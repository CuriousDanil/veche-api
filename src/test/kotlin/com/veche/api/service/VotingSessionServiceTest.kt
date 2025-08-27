package com.veche.api.service

import com.veche.api.database.model.*
import com.veche.api.database.repository.DiscussionRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.VotingSessionRepository
import com.veche.api.dto.votingSession.VotingSessionRequestDto
import com.veche.api.dto.votingSession.VotingSessionResponseDto
import com.veche.api.dto.votingSession.VotingSessionUpdateDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.VotingSessionMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*

class VotingSessionServiceTest {
    private lateinit var votingSessionService: VotingSessionService
    private lateinit var votingSessionRepository: VotingSessionRepository
    private lateinit var discussionRepository: DiscussionRepository
    private lateinit var partyRepository: PartyRepository
    private lateinit var votingSessionMapper: VotingSessionMapper
    private lateinit var summaryService: SummaryService

    @BeforeEach
    fun setUp() {
        votingSessionRepository = mock()
        discussionRepository = mock()
        partyRepository = mock()
        votingSessionMapper = mock()
        summaryService = mock()
        votingSessionService =
            VotingSessionService(
                votingSessionRepository,
                discussionRepository,
                partyRepository,
                votingSessionMapper,
                summaryService,
            )
    }

    @Test
    fun `getAllVotingSessions should return all voting sessions for user's company`() {
        // Given
        val userId = UUID.randomUUID()
        val companyId = UUID.randomUUID()
        val session1 =
            VotingSessionEntity().apply {
                name = "Session 1"
                status = VotingSessionStatus.WAITING
            }
        val session2 =
            VotingSessionEntity().apply {
                name = "Session 2"
                status = VotingSessionStatus.VOTING
            }
        val sessions = listOf(session1, session2)
        val sessionResponses =
            listOf(
                VotingSessionResponseDto(
                    id = UUID.randomUUID(),
                    name = "Session 1",
                    party = mock(),
                    status = VotingSessionStatus.WAITING,
                    discussions = listOf(),
                    firstRoundStart = null,
                    secondRoundStart = null,
                    endTime = null,
                ),
                VotingSessionResponseDto(
                    id = UUID.randomUUID(),
                    name = "Session 2",
                    party = mock(),
                    status = VotingSessionStatus.VOTING,
                    discussions = listOf(),
                    firstRoundStart = null,
                    secondRoundStart = null,
                    endTime = null,
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

        whenever(votingSessionRepository.findAllByPartyCompanyId(companyId)).thenReturn(sessions)
        whenever(votingSessionMapper.toDto(session1)).thenReturn(sessionResponses[0])
        whenever(votingSessionMapper.toDto(session2)).thenReturn(sessionResponses[1])

        // When
        val result = votingSessionService.getAllVotingSessions(userPrincipal)

        // Then
        assertEquals(2, result.size)
        assertEquals(sessionResponses, result)
        verify(votingSessionRepository).findAllByPartyCompanyId(companyId)
        verify(votingSessionMapper, times(2)).toDto(any<VotingSessionEntity>())
    }

    @Test
    fun `startVotingSession should update session and discussions status to VOTING`() {
        // Given
        val sessionId = UUID.randomUUID()
        val discussion1 =
            DiscussionEntity().apply {
                status = DiscussionStatus.WAITING
            }
        val discussion2 =
            DiscussionEntity().apply {
                status = DiscussionStatus.WAITING
            }
        val session =
            VotingSessionEntity().apply {
                status = VotingSessionStatus.WAITING
                discussions = mutableSetOf(discussion1, discussion2)
            }

        whenever(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session))

        // When
        votingSessionService.startVotingSession(sessionId)

        // Then
        assertEquals(VotingSessionStatus.VOTING, session.status)
        assertEquals(DiscussionStatus.VOTING, discussion1.status)
        assertEquals(DiscussionStatus.VOTING, discussion2.status)
        verify(votingSessionRepository).findById(sessionId)
    }

    @Test
    fun `startVotingSession should throw NotFoundException when session not found`() {
        // Given
        val sessionId = UUID.randomUUID()

        whenever(votingSessionRepository.findById(sessionId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            votingSessionService.startVotingSession(sessionId)
        }
    }

    @Test
    fun `startVotingSessionSecondRound should update session to FINAL_VOTING`() {
        // Given
        val sessionId = UUID.randomUUID()
        val session =
            VotingSessionEntity().apply {
                status = VotingSessionStatus.VOTING
                secondRoundStartsAt = null
            }

        whenever(votingSessionRepository.findById(sessionId)).thenReturn(Optional.of(session))

        // When
        votingSessionService.startVotingSessionSecondRound(sessionId)

        // Then
        assertEquals(VotingSessionStatus.FINAL_VOTING, session.status)
        assertNotNull(session.secondRoundStartsAt)
        verify(votingSessionRepository).findById(sessionId)
    }

    @Test
    fun `startVotingSessionSecondRound should throw NotFoundException when session not found`() {
        // Given
        val sessionId = UUID.randomUUID()

        whenever(votingSessionRepository.findById(sessionId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            votingSessionService.startVotingSessionSecondRound(sessionId)
        }
    }
}

