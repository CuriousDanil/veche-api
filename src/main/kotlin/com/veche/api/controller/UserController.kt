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

/**
 * TODO()
 *
 * @property userService TODO()
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    /**
     * TODO()
     *
     * @param user TODO()
     * @return TODO()
     */
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: UserPrincipal,
    ) = userService.getCurrentUser(user)

    /**
     * TODO()
     *
     * @param user TODO()
     * @param userId TODO()
     * @return TODO()
     */
    @GetMapping("/{userId}")
    fun getUser(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable userId: UUID,
    ) = userService.getUserById(user, userId)

    /**
     * TODO()
     *
     * @param user TODO()
     * @param request TODO()
     * @return TODO()
     */
    @GetMapping("/search")
    fun searchUsersForUserCompany(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserSearchRequestDto,
    ) = userService.searchUsersForUserCompany(request, user)

    /**
     * TODO()
     *
     * @param user TODO()
     * @param request TODO()
     * @return TODO()
     */
    @PostMapping
    fun updateUserCredentials(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserUpdateCredentialsDto,
    ) = userService.updateUserCredentials(request, user)
}
