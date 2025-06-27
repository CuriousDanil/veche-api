package com.veche.api.dto.company

import java.time.Instant
import java.util.UUID

/**
 * Data Transfer Object for company responses.
 *
 * @property id The unique identifier of the company
 * @property name The name of the company
 * @property createdAt Timestamp indicating when the company was created
 * @property updatedAt Timestamp indicating when the company was last updated
 */
data class CompanyResponseDto(
    val id: UUID,
    val name: String
)
