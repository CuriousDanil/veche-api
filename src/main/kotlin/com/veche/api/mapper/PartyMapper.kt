package com.veche.api.mapper

import com.veche.api.database.model.PartyEntity
import com.veche.api.dto.party.PartyResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper to convert between PartyEntity and DTOs.
 */
@Component
class PartyMapper {
    fun toDto(entity: PartyEntity): PartyResponseDto =
        PartyResponseDto(
            id = entity.id,
            name = entity.name,
        )
}
