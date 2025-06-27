package com.veche.api.dto.party

/**
 * Data Transfer Object for updating an existing party.
 *
 * @property name The new name for the party
 */
data class PartyUpdateDto(
    val name: String
)
