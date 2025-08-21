package com.veche.api.service

import com.veche.api.database.model.InvitationEntity
import com.veche.api.database.repository.InvitationRepository
import com.veche.api.database.repository.PartyRepository
import com.veche.api.database.repository.UserRepository
import com.veche.api.dto.invitation.InvitationCreateResponseDto
import com.veche.api.dto.invitation.InvitationRequestDto
import com.veche.api.dto.invitation.InvitationResponseDto
import com.veche.api.exception.NotFoundException
import com.veche.api.security.UserPrincipal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.Base64

@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val userRepository: UserRepository,
    private val partyRepository: PartyRepository,
) {
    private val rng = SecureRandom()

    fun randomToken(bytes: Int = 48): String {
        val b = ByteArray(bytes)
        rng.nextBytes(b)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b)
    }

    @Transactional
    fun createInvitation(
        user: UserPrincipal,
        dto: InvitationRequestDto,
    ): InvitationCreateResponseDto {
        val invitation =
            invitationRepository.save(
                InvitationEntity().apply {
                    token = randomToken(48)
                    suggestedName = dto.name
                    suggestedBio = dto.bio
                    suggestedEmail = dto.email
                    party =
                        partyRepository
                            .findById(dto.partyId)
                            .orElseThrow { NotFoundException("Party ${dto.partyId} not found") }
                    creator =
                        userRepository
                            .findById(user.id)
                            .orElseThrow { NotFoundException("User ${user.id} not found") }
                },
            )
        return InvitationCreateResponseDto(
            url = "https://localhost:5173/invitations/${invitation.token}",
        )
    }

    @Transactional(readOnly = true)
    fun getInvitation(token: String): InvitationResponseDto {
        val invitation = invitationRepository.findByToken(token) ?: throw NotFoundException("Invitation is not found")
        return InvitationResponseDto(
            partyId = invitation.party.id,
            companyName = invitation.party.company.name,
            suggestedName = invitation.suggestedName,
            suggestedBio = invitation.suggestedBio,
            suggestedEmail = invitation.suggestedEmail,
            expiresAt = invitation.expiresAt,
        )
    }
}
