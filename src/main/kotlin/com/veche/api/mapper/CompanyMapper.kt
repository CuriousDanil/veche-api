package com.veche.api.mapper

import com.veche.api.database.model.CompanyEntity
import com.veche.api.dto.company.CompanyResponseDto
import org.springframework.stereotype.Component

@Component
class CompanyMapper(
    private val userMapper: UserMapper,
    private val partyMapper: PartyMapper,
) {
    fun toDto(entity: CompanyEntity): CompanyResponseDto =
        CompanyResponseDto(
            id = entity.id,
            name = entity.name,
            users = entity.users.map(userMapper::toDto),
            parties = entity.parties.map(partyMapper::toDto),
        )
}