package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.ImageDto;
import com.gallery.fineart.mfineart.exception.event.EventNotFoundException;
import com.gallery.fineart.mfineart.exception.painting.PaintingNotFoundException;
import com.gallery.fineart.mfineart.model.Event;
import com.gallery.fineart.mfineart.model.Image;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.EventRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ImageMapper {

    private final PaintingRepository paintingRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ImageMapper(PaintingRepository paintingRepository, EventRepository eventRepository) {
        this.paintingRepository = paintingRepository;
        this.eventRepository = eventRepository;
    }

    @Mapping(target = "painting", source = "java(fetchPainting(imageDto.getPaintingId()))")
    @Mapping(target = "event", source = "java(fetchEvent(imageDto.getEventId()))")
    public abstract Image toImage(ImageDto imageDto);

    @Mapping(target = "paintingId", expression = "java(image.getPainting() != null ? image.getPainting().getId() : null)")
    @Mapping(target = "eventId", expression = "java(image.getEvent() != null ? image.getEvent().getId() : null)")
    public abstract ImageDto toImageDto(Image image);

    private Painting fetchPainting(Long paintingId) {
        if (paintingId == null) {
            return null;
        }
        try {
            return paintingRepository.findById(paintingId)
                    .orElseThrow(() -> new PaintingNotFoundException(String.valueOf(paintingId)));
        } catch (PaintingNotFoundException e) {
            return null;
        }
    }

    private Event fetchEvent(Long eventId) {
        if (eventId == null) {
            return null;
        }
        try {
            return eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException(String.valueOf(eventId)));
        } catch (EventNotFoundException e) {
            return null;
        }
    }
}
