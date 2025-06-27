package com.veche.api.mapper

import com.veche.api.database.model.CompanyEntity
import com.veche.api.dto.company.CompanyResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper to convert between CompanyEntity and DTOs.
 */
@Component
class CompanyMapper {

    /**
     * Converts a CompanyEntity to CompanyResponseDto.
     *
     * @param entity The company entity to be converted
     * @return A data transfer object representing the company
     */
    fun toDto(entity: CompanyEntity): CompanyResponseDto {
        return CompanyResponseDto(
            id = entity.id,
            name = entity.name
        )
    }
}
