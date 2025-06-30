package com.veche.api.dto.action

import java.util.UUID

data class ActionResponseDto(
    val id: UUID,
    val actionType: String,
    val payload: String,
)
