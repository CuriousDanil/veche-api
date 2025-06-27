package com.veche.api.mapper

import com.veche.api.database.model.PartyEntity
import com.veche.api.dto.party.PartyResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper to convert between PartyEntity and DTOs.
 */
@Component
class PartyMapper {

    /**
     * Converts a PartyEntity to PartyResponseDto.
     *
     * @param entity The party entity to be converted
     * @return A data transfer object representing the party
     */
    fun toDto(entity: PartyEntity): PartyResponseDto {
        return PartyResponseDto(
            id = entity.id,
            name = entity.name
        )
    }
}
