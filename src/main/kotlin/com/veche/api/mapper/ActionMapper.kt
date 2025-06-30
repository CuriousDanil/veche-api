package com.veche.api.mapper

import com.veche.api.database.model.PendingActionEntity
import com.veche.api.dto.action.ActionResponseDto
import org.springframework.stereotype.Component

@Component
class ActionMapper {
    fun toDto(entity: PendingActionEntity): ActionResponseDto =
        ActionResponseDto(
            id = entity.id,
            actionType = entity.actionType.name,
            payload = entity.payload,
        )
}
