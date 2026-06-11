package com.gallery.fineart.mfineart.service.event;

import com.gallery.fineart.mfineart.dto.EventDto;
import com.gallery.fineart.mfineart.model.Event;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface EventService {
    List<EventDto> getAllEvents(boolean sorted);

    EventDto getEventById(String id);

    Event findEventById(String id);

    Event addEvent(@Valid EventDto eventDto);

    Event addEvent(@Valid EventDto eventDto, Map<MultipartFile, Boolean> imagesFiles);

    Long editEvent(@Valid EventDto eventDto);

    Boolean deleteEventById(String eventId);
}
