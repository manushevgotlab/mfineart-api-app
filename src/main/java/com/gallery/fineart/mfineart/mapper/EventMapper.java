package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.EventDto;
import com.gallery.fineart.mfineart.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);
}
