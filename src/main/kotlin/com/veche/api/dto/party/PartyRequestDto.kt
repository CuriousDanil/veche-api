package com.veche.api.dto.party

/**
 * Data Transfer Object for creating a new party.
 *
 * @property name The name of the party to be created.
 */
data class PartyRequestDto(
    val name: String,
)
