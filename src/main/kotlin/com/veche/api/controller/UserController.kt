package com.veche.api.controller

import com.veche.api.dto.user.UserSearchRequestDto
import com.veche.api.dto.user.UserUpdateCredentialsDto
import com.veche.api.security.UserPrincipal
import com.veche.api.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: UserPrincipal,
    ) = userService.getCurrentUser(user)

    @GetMapping("/{userId}")
    fun getUser(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable userId: UUID,
    ) = userService.getUserById(user, userId)

    @GetMapping("/search")
    fun searchUsersForUserCompany(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserSearchRequestDto,
    ) = userService.searchUsersForUserCompany(request, user)

    @PostMapping
    fun updateUserCredentials(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserUpdateCredentialsDto,
    ) = userService.updateUserCredentials(request, user)
}
