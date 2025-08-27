package com.veche.api.config

import com.veche.api.database.model.DiscussionStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * TODO()
 */
@Component
class DiscussionStatusConverter : Converter<String, DiscussionStatus> {
    /**
     * TODO()
     *
     * @param source TODO()
     * @return TODO()
     */
    override fun convert(source: String): DiscussionStatus =
        try {
            DiscussionStatus.valueOf(source.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid discussion status: $source")
        }
}
