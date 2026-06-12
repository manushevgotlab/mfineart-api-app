package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.EventDto;
import com.gallery.fineart.mfineart.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "contentStatus", ignore = true)
    @Mapping(target = "publishAt", ignore = true)
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);
}
