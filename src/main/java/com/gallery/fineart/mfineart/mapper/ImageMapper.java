package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.ImageDto;
import com.gallery.fineart.mfineart.exception.collection.CollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.event.EventNotFoundException;
import com.gallery.fineart.mfineart.exception.painting.PaintingNotFoundException;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Event;
import com.gallery.fineart.mfineart.model.Image;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import com.gallery.fineart.mfineart.repository.EventRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ImageMapper {

    private final CollectionRepository collectionRepository;
    private final PaintingRepository paintingRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ImageMapper(CollectionRepository collectionRepository, PaintingRepository paintingRepository, EventRepository eventRepository) {
        this.collectionRepository = collectionRepository;
        this.paintingRepository = paintingRepository;
        this.eventRepository = eventRepository;
    }

    @Mapping(target = "painting", source = "java(fetchPainting(imageDto.getPaintingId()))")
    @Mapping(target = "collection", source = "java(fetchCollection(imageDto.getCollectionId()))")
    @Mapping(target = "event", source = "java(fetchEvent(imageDto.getEventId()))")
    public abstract Image toImage(ImageDto imageDto);

    @Mapping(target = "paintingId", source = "java(image.getPainting().getId()")
    @Mapping(target = "collectionId", source = "java(image.getCollection().getId()")
    @Mapping(target = "eventId", source = "java(image.getEvent().getId()")
    public abstract ImageDto toImageDto(Image image);

    private Painting fetchPainting(Long paintingId) {
        try {
            return paintingRepository.findById(paintingId)
                    .orElseThrow(() -> new PaintingNotFoundException(String.valueOf(paintingId)));
        } catch (PaintingNotFoundException e) {
            return null;
        }
    }

    private ArtCollection fetchCollection(Long collectionId) {
        try {
            return collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new CollectionNotFoundException(String.valueOf(collectionId)));
        } catch (CollectionNotFoundException e) {
            return null;
        }
    }

    private Event fetchEvent(Long eventId) {
        try {
            return eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException(String.valueOf(eventId)));
        } catch (EventNotFoundException e) {
            return null;
        }
    }
}
