package com.veche.api.config

import com.veche.api.database.model.DiscussionStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class DiscussionStatusConverter : Converter<String, DiscussionStatus> {

    override fun convert(source: String): DiscussionStatus {
        return try {
            DiscussionStatus.valueOf(source.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid discussion status: $source")
        }
    }
}