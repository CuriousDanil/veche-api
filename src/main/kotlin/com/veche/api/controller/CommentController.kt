package com.veche.api.controller

import com.veche.api.dto.comment.CommentRequestDto
import com.veche.api.dto.comment.CommentResponseDto
import com.veche.api.dto.comment.CommentUpdateDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class CommentController(
    private val commentService: CommentService,
) {
    @PostMapping("/discussions/{discussionId}/comments")
    fun createComment(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable discussionId: UUID,
        @RequestBody request: CommentRequestDto,
    ): CommentResponseDto = commentService.createComment(user, discussionId, request)

    @GetMapping("/discussions/{discussionId}/comments")
    fun getCommentsForDiscussion(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable discussionId: UUID,
    ): List<CommentResponseDto> = commentService.getCommentsForDiscussion(user, discussionId)

    @PatchMapping("/comments/{commentId}")
    fun updateComment(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable commentId: UUID,
        @RequestBody updateDto: CommentUpdateDto,
    ): ResponseEntity<CommentResponseDto> = ResponseEntity.ok(commentService.updateComment(user, commentId, updateDto))
}
