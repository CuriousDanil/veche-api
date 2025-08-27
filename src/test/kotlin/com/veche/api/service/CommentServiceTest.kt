package com.veche.api.service

import com.veche.api.database.model.*
import com.veche.api.database.repository.CommentRepository
import com.veche.api.database.repository.DiscussionRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.comment.CommentRequestDto
import com.veche.api.dto.comment.CommentResponseDto
import com.veche.api.dto.comment.CommentUpdateDto
import com.veche.api.dto.user.UserResponseDto
import com.veche.api.exception.ForbiddenException
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.CommentMapper
import com.veche.api.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*

class CommentServiceTest {
    private lateinit var commentService: CommentService
    private lateinit var commentRepository: CommentRepository
    private lateinit var discussionRepository: DiscussionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var commentMapper: CommentMapper

    @BeforeEach
    fun setUp() {
        commentRepository = mock()
        discussionRepository = mock()
        userRepository = mock()
        commentMapper = mock()
        commentService = CommentService(commentRepository, discussionRepository, userRepository, commentMapper)
    }

    @Test
    fun `createComment should create COMMENT type when discussion is WAITING`() {
        // Given
        val userId = UUID.randomUUID()
        val discussionId = UUID.randomUUID()
        val request = CommentRequestDto(content = "Test comment content")

        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }

        val discussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                status = DiscussionStatus.WAITING
            }

        val savedComment =
            CommentEntity().apply {
                content = "Test comment content"
                this.discussion = discussion
                creator = user
                commentType = CommentType.COMMENT
            }

        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        val commentDto =
            CommentResponseDto(
                id = UUID.randomUUID(),
                content = "Test comment content",
                creator = userResponseDto,
                status = CommentType.COMMENT,
                createdAt = Instant.now(),
            )

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(discussionRepository.findById(discussionId)).thenReturn(Optional.of(discussion))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(
            commentRepository.existsByDiscussionIdAndCreatorIdAndCommentType(
                discussionId,
                userId,
                CommentType.COMMENT,
            ),
        ).thenReturn(false)
        whenever(commentRepository.save(any<CommentEntity>())).thenReturn(savedComment)
        whenever(commentMapper.toDto(savedComment)).thenReturn(commentDto)

        // When
        val result = commentService.createComment(userPrincipal, discussionId, request)

        // Then
        assertEquals(commentDto, result)
        verify(discussionRepository).findById(discussionId)
        verify(userRepository).findById(userId)
        verify(commentRepository).existsByDiscussionIdAndCreatorIdAndCommentType(
            discussionId,
            userId,
            CommentType.COMMENT,
        )
        verify(commentRepository).save(any<CommentEntity>())
        verify(commentMapper).toDto(savedComment)
    }

    @Test
    fun `createComment should create ARGUMENT type when discussion is VOTING`() {
        // Given
        val userId = UUID.randomUUID()
        val discussionId = UUID.randomUUID()
        val request = CommentRequestDto(content = "Test argument content")

        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }

        val discussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                status = DiscussionStatus.VOTING
            }

        val savedComment =
            CommentEntity().apply {
                content = "Test argument content"
                this.discussion = discussion
                creator = user
                commentType = CommentType.ARGUMENT
            }

        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        val commentDto =
            CommentResponseDto(
                id = UUID.randomUUID(),
                content = "Test argument content",
                creator = userResponseDto,
                status = CommentType.ARGUMENT,
                createdAt = Instant.now(),
            )

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(discussionRepository.findById(discussionId)).thenReturn(Optional.of(discussion))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(
            commentRepository.existsByDiscussionIdAndCreatorIdAndCommentType(
                discussionId,
                userId,
                CommentType.ARGUMENT,
            ),
        ).thenReturn(false)
        whenever(commentRepository.save(any<CommentEntity>())).thenReturn(savedComment)
        whenever(commentMapper.toDto(savedComment)).thenReturn(commentDto)

        // When
        val result = commentService.createComment(userPrincipal, discussionId, request)

        // Then
        assertEquals(commentDto, result)
        verify(commentRepository).existsByDiscussionIdAndCreatorIdAndCommentType(
            discussionId,
            userId,
            CommentType.ARGUMENT,
        )
    }

    @Test
    fun `createComment should throw ForbiddenException when user already posted comment of same type`() {
        // Given
        val userId = UUID.randomUUID()
        val discussionId = UUID.randomUUID()
        val request = CommentRequestDto(content = "Test comment content")

        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }

        val discussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                status = DiscussionStatus.WAITING
            }

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(discussionRepository.findById(discussionId)).thenReturn(Optional.of(discussion))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(
            commentRepository.existsByDiscussionIdAndCreatorIdAndCommentType(
                discussionId,
                userId,
                CommentType.COMMENT,
            ),
        ).thenReturn(true)

        // When/Then
        assertThrows<ForbiddenException> {
            commentService.createComment(userPrincipal, discussionId, request)
        }
    }

    @Test
    fun `createComment should throw NotFoundException when discussion not found`() {
        // Given
        val userId = UUID.randomUUID()
        val discussionId = UUID.randomUUID()
        val request = CommentRequestDto(content = "Test comment content")

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(discussionRepository.findById(discussionId)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<NotFoundException> {
            commentService.createComment(userPrincipal, discussionId, request)
        }
    }

    @Test
    fun `getCommentsForDiscussion should return comments when discussion is accessible`() {
        // Given
        val userId = UUID.randomUUID()
        val discussionId = UUID.randomUUID()

        val discussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                status = DiscussionStatus.RESOLVED
            }

        val comment =
            CommentEntity().apply {
                content = "Test Comment"
                commentType = CommentType.ARGUMENT
            }
        val allComments = listOf(comment)

        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        val commentDto =
            CommentResponseDto(
                id = UUID.randomUUID(),
                content = "Test Comment",
                creator = userResponseDto,
                status = CommentType.ARGUMENT,
                createdAt = Instant.now(),
            )

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(discussionRepository.findById(discussionId)).thenReturn(Optional.of(discussion))
        whenever(commentRepository.findAllByDiscussionId(discussionId)).thenReturn(allComments)
        whenever(commentMapper.toDto(comment)).thenReturn(commentDto)

        // When
        val result = commentService.getCommentsForDiscussion(userPrincipal, discussionId)

        // Then
        assertEquals(1, result.size)
        assertEquals(listOf(commentDto), result)
        verify(discussionRepository).findById(discussionId)
        verify(commentRepository).findAllByDiscussionId(discussionId)
        verify(commentMapper).toDto(comment)
    }

    @Test
    fun `updateComment should update comment when user is creator`() {
        // Given
        val userId = UUID.randomUUID()
        val commentId = UUID.randomUUID()
        val updateDto = CommentUpdateDto(content = "Updated content")

        val user =
            UserEntity().apply {
                name = "Test User"
                email = "test@example.com"
            }

        val discussion =
            DiscussionEntity().apply {
                subject = "Test Discussion"
                status = DiscussionStatus.WAITING
            }

        val comment =
            CommentEntity().apply {
                content = "Original content"
                creator = user
                this.discussion = discussion
                commentType = CommentType.COMMENT
            }

        val userResponseDto =
            UserResponseDto(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                bio = "",
                parties = listOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        val updatedCommentDto =
            CommentResponseDto(
                id = commentId,
                content = "Updated content",
                creator = userResponseDto,
                status = CommentType.COMMENT,
                createdAt = Instant.now(),
            )

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        whenever(commentRepository.save(comment)).thenReturn(comment)
        whenever(commentMapper.toDto(comment)).thenReturn(updatedCommentDto)

        // When
        val result = commentService.updateComment(userPrincipal, commentId, updateDto)

        // Then
        assertEquals(updatedCommentDto, result)
        assertEquals("Updated content", comment.content)
        verify(commentRepository).findById(commentId)
        verify(commentRepository).save(comment)
        verify(commentMapper).toDto(comment)
    }

    @Test
    fun `updateComment should throw ForbiddenException when user is not creator`() {
        // Given
        val userId = UUID.randomUUID()
        val otherUserId = UUID.randomUUID()
        val commentId = UUID.randomUUID()
        val updateDto = CommentUpdateDto(content = "Updated content")

        val otherUser =
            UserEntity().apply {
                name = "Other User"
                email = "other@example.com"
            }

        val comment =
            CommentEntity().apply {
                content = "Original content"
                creator = otherUser
            }

        val userPrincipal =
            UserPrincipal(
                id = userId,
                companyId = UUID.randomUUID(),
                partyIds = setOf(),
                isAbleToPostDiscussions = false,
                isAbleToManageSessions = false,
                isAbleToManageUsers = false,
            )

        whenever(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))

        // When/Then
        assertThrows<ForbiddenException> {
            commentService.updateComment(userPrincipal, commentId, updateDto)
        }
    }
}

