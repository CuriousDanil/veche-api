package com.veche.api.event

import java.util.UUID

data class DiscussionResolvedEvent(
    val discussionId: UUID,
    val approved: Boolean
)