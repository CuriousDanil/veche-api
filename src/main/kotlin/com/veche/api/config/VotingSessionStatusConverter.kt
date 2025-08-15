package com.veche.api.config

import com.veche.api.database.model.VotingSessionStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class VotingSessionStatusConverter : Converter<String, VotingSessionStatus> {
    override fun convert(source: String): VotingSessionStatus =
        try {
            VotingSessionStatus.valueOf(source.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid voting session status: $source")
        }
}