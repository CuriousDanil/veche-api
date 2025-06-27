package com.veche.api.mapper

import com.veche.api.database.model.DiscussionEntity
import com.veche.api.dto.discussion.DiscussionResponseDto
import org.mapstruct.BeanMapping
import org.mapstruct.InheritConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
abstract class DiscussionMapper {

    @Mappings(
        Mapping(source = "party.id", target = "partyId"),
        Mapping(source = "creator.name", target = "creatorName")
    )
    abstract fun toDto(discussionEntity: DiscussionEntity): DiscussionResponseDto

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun partialUpdate(
        discussionResponseDto: DiscussionResponseDto,
        @MappingTarget discussionEntity: DiscussionEntity
    ): DiscussionEntity
}