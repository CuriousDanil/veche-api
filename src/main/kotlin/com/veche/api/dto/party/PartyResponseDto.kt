package com.veche.api.dto.party

import java.time.Instant
import java.util.UUID

/**
 * Data Transfer Object for party responses.
 *
 * @property id The unique identifier of the party
 * @property name The name of the party
 * @property companyId The ID of the company to which the party belongs
 * @property createdAt Timestamp indicating when the party was created
 * @property updatedAt Timestamp indicating when the party was last updated
 */
data class PartyResponseDto(
    val id: UUID,
    val name: String
)
