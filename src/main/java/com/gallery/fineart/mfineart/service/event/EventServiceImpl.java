package com.gallery.fineart.mfineart.service.event;

import com.gallery.fineart.mfineart.dto.EventDto;
import com.gallery.fineart.mfineart.exception.event.EventNotFoundException;
import com.gallery.fineart.mfineart.exception.image.InvalidImagesThumbnailCountException;
import com.gallery.fineart.mfineart.mapper.EventMapper;
import com.gallery.fineart.mfineart.model.Event;
import com.gallery.fineart.mfineart.repository.EventRepository;
import com.gallery.fineart.mfineart.service.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final long EXACT_NUMBER_OF_THUMBNAILS_PER_SET_OF_IMAGES = 1;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ImageService imageService;

    @Autowired
    public EventServiceImpl(final EventRepository eventRepository,
                            final EventMapper eventMapper,
                            final ImageService imageService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.imageService = imageService;
    }

    @Override
    public List<EventDto> getAllEvents(boolean sorted) {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toEventDto)
                .sorted(sorted ? Comparator.comparing(EventDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventById(String id) {
        return eventMapper.toEventDto(findEventById(id));
    }

    @Override
    public Event findEventById(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Parameter ID cannot be null");
        }

        Optional<Event> eventOptional = eventRepository.findById(Long.parseLong(id));

        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException(id);
        }

        return eventOptional.get();
    }

    @Override
    public Event addEvent(EventDto eventDto) {
        validateEventDto(eventDto);

        Event event = eventMapper.toEvent(eventDto);
        eventRepository.save(event);

        return event;
    }

    @Override
    public Event addEvent(EventDto eventDto, Map<MultipartFile, Boolean> imagesFiles) {
        if (Objects.isNull(imagesFiles) || imagesFiles.isEmpty()) {
            throw new IllegalArgumentException("Image files cannot be null");
        }

        validateImagesHaveThumbnail(imagesFiles.values());

        Event event = addEvent(eventDto);

        for (Map.Entry<MultipartFile, Boolean> entry : imagesFiles.entrySet()) {
            MultipartFile imageFile = entry.getKey();
            Boolean isThumbnail = entry.getValue();
            imageService.addImageForEntity(imageFile, isThumbnail, event);
        }

        return event;
    }

    @Override
    public Long editEvent(EventDto eventDto) {
        validateEventDto(eventDto);
        if (eventDto.getId() == null) {
            throw new IllegalArgumentException("Parameter Id of PaintingDto cannot be null.");
        }
        Optional<Event> eventOptional = eventRepository.findById(eventDto.getId());
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException(String.valueOf(eventDto.getId()));
        }
        Event event = eventOptional.get();
        event.setName(eventDto.getName());
        event.setDescription(eventDto.getDescription());
        event.setEventType(eventDto.getEventType());

        if (Objects.nonNull(eventDto.getDate())) {
            event.setDate(eventDto.getDate());
        }

        eventRepository.save(event);

        return event.getId();
    }

    @Override
    public Boolean deleteEventById(String eventId) {
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("Parameter Id cannot be null.");
        }

        Optional<Event> eventOptional = eventRepository.findById(Long.valueOf(eventId));
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
        Event event = eventOptional.get();

        eventRepository.delete(event);

        return true;
    }

    private void validateEventDto(EventDto eventDto) {
        if (Objects.isNull(eventDto)) {
            throw new IllegalArgumentException("Parameter EventDto cannot be null.");
        }
    }

    private void validateImagesHaveThumbnail(Collection<Boolean> imagesThumbnails) {
        long thumbnailsCount = imagesThumbnails.stream()
                .filter(isThumbnail -> isThumbnail)
                .count();

        if (thumbnailsCount != EXACT_NUMBER_OF_THUMBNAILS_PER_SET_OF_IMAGES) {
            throw new InvalidImagesThumbnailCountException(imagesThumbnails.size(), thumbnailsCount);
        }
    }

}
