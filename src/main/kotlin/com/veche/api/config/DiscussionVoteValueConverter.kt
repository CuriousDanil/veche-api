package com.veche.api.config

import com.veche.api.database.model.VoteValue
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * TODO()
 */
@Component
class DiscussionVoteValueConverter : Converter<String, VoteValue> {
    /**
     * TODO()
     *
     * @param source TODO()
     * @return TODO()
     */
    override fun convert(source: String): VoteValue =
        try {
            VoteValue.valueOf(source.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid discussion vote value: $source")
        }
}
