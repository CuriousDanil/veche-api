package com.veche.api.mapper

import com.veche.api.database.model.SummaryEntity
import com.veche.api.dto.summary.SummaryResponseDto
import org.springframework.stereotype.Component

@Component
class SummaryMapper {
    fun toDto(entity: SummaryEntity): SummaryResponseDto =
        SummaryResponseDto(
            id = entity.id,
            content = entity.content,
        )
}
