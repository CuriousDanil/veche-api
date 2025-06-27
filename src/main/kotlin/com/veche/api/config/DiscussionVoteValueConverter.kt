package com.veche.api.config

import com.veche.api.database.model.VoteValue
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class DiscussionVoteValueConverter : Converter<String, VoteValue> {

    override fun convert(source: String): VoteValue {
        return try {
            VoteValue.valueOf(source.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid discussion vote value: $source")
        }
    }
}