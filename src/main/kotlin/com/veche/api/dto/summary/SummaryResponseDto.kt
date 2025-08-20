package com.veche.api.dto.summary

import java.util.UUID

data class SummaryResponseDto(
    val id: UUID,
    val content: String,
)
