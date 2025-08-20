package com.veche.api.event

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.veche.api.database.model.ActionType
import org.springframework.stereotype.Component
import java.util.UUID

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ChangePartyName::class, name = "RENAME_PARTY"),
    JsonSubTypes.Type(value = ChangeCompanyName::class, name = "RENAME_COMPANY"),
    JsonSubTypes.Type(value = EvictUserFromParty::class, name = "EVICT_USER_FROM_PARTY"),
    JsonSubTypes.Type(value = AddUserToParty::class, name = "ADD_USER_TO_PARTY"),
    JsonSubTypes.Type(value = DeleteParty::class, name = "DELETE_PARTY"),
)
sealed interface ActionPayload {
    val type: ActionType
}

data class DeleteParty(
    override val type: ActionType = ActionType.DELETE_PARTY,
    val partyId: UUID,
) : ActionPayload

data class ChangePartyName(
    override val type: ActionType = ActionType.RENAME_PARTY,
    val partyId: UUID,
    val newName: String,
) : ActionPayload

data class ChangeCompanyName(
    override val type: ActionType = ActionType.RENAME_COMPANY,
    val companyId: UUID,
    val newName: String,
) : ActionPayload

data class EvictUserFromParty(
    override val type: ActionType = ActionType.EVICT_USER_FROM_PARTY,
    val partyId: UUID,
    val userId: UUID,
) : ActionPayload

data class AddUserToParty(
    override val type: ActionType = ActionType.ADD_USER_TO_PARTY,
    val partyId: UUID,
    val userId: UUID,
) : ActionPayload

@Component
class PayloadMapper(
    private val mapper: ObjectMapper,
) {
    fun toJson(payload: ActionPayload): String = mapper.writeValueAsString(payload)

    fun <T : ActionPayload> fromJson(
        json: String,
        clazz: Class<T>,
    ): T = mapper.readValue(json, clazz)
}
