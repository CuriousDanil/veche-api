package com.veche.api.service

import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.user.UserResponseDto
import com.veche.api.dto.user.UserSearchRequestDto
import com.veche.api.dto.user.UserUpdateCredentialsDto
import com.veche.api.exception.NotFoundException
import com.veche.api.mapper.UserMapper
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * TODO()
 *
 * @property userRepository TODO()
 * @property userMapper TODO()
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
) {
    /**
     * TODO()
     *
     * @param user TODO()
     * @param userId TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun getUserById(
        user: UserPrincipal,
        userId: UUID,
    ): UserResponseDto =
        userRepository
            .findById(userId)
            .takeIf {
                userRepository
                    .findById(user.id)
                    .orElseThrow { NotFoundException("Authenticated user not found") }
                    .company ==
                    userRepository
                        .findById(
                            userId,
                        ).orElseThrow { NotFoundException("User not found") }
            }?.map(userMapper::toDto)
            ?.orElseThrow { NoSuchElementException("User not found") } ?: throw NotFoundException("User not found")

    /**
     * TODO()
     *
     * @param user TODO()
     * @return TODO()
     */
    @Transactional
    fun getCurrentUser(user: UserPrincipal): UserResponseDto =
        userRepository
            .findById(user.id)
            .map(userMapper::toDto)
            .orElseThrow { NotFoundException("Authenticated user not found.") }

    /**
     * TODO()
     *
     * @param dto TODO()
     * @param user TODO()
     * @return TODO()
     */
    @Transactional(readOnly = true)
    fun searchUsersForUserCompany(
        dto: UserSearchRequestDto,
        user: UserPrincipal,
    ): List<UserResponseDto> =
        userRepository
            .search(
                userRepository.getReferenceById(user.id).company,
                dto.query,
            ).map(userMapper::toDto)

    /**
     * TODO()
     *
     * @param dto TODO()
     * @param user TODO()
     * @return TODO()
     */
    @Transactional
    fun updateUserCredentials(
        dto: UserUpdateCredentialsDto,
        user: UserPrincipal,
    ): UserResponseDto {
        val userEntity = userRepository.getReferenceById(user.id)

        userEntity.name = dto.name ?: userEntity.name
        userEntity.bio = dto.bio ?: userEntity.bio

        return userMapper.toDto(userEntity)
    }
}
