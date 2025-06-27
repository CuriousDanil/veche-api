package com.veche.api.dto.discussion

data class DiscussionUpdateDto(
    val subject: String? = null,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null
)