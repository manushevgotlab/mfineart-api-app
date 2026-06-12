package com.gallery.fineart.mfineart.service.content;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Event;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import com.gallery.fineart.mfineart.repository.EventRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledPublishingService {

    private final PaintingRepository paintingRepository;
    private final EventRepository eventRepository;
    private final CollectionRepository collectionRepository;

    public ScheduledPublishingService(PaintingRepository paintingRepository,
                                      EventRepository eventRepository,
                                      CollectionRepository collectionRepository) {
        this.paintingRepository = paintingRepository;
        this.eventRepository = eventRepository;
        this.collectionRepository = collectionRepository;
    }

    @Scheduled(cron = "${app.content.scheduled-publish-cron:0 * * * * *}")
    @Transactional
    public void publishDueContent() {
        LocalDateTime now = LocalDateTime.now();
        publishDuePaintings(now);
        publishDueEvents(now);
        publishDueCollections(now);
    }

    private void publishDuePaintings(LocalDateTime now) {
        List<Painting> duePaintings = paintingRepository
                .findByContentStatusAndPublishAtLessThanEqual(ContentStatus.SCHEDULED, now);
        duePaintings.forEach(painting -> painting.setContentStatus(ContentStatus.PUBLISHED));
        paintingRepository.saveAll(duePaintings);
    }

    private void publishDueEvents(LocalDateTime now) {
        List<Event> dueEvents = eventRepository
                .findByContentStatusAndPublishAtLessThanEqual(ContentStatus.SCHEDULED, now);
        dueEvents.forEach(event -> event.setContentStatus(ContentStatus.PUBLISHED));
        eventRepository.saveAll(dueEvents);
    }

    private void publishDueCollections(LocalDateTime now) {
        List<ArtCollection> dueCollections = collectionRepository
                .findByContentStatusAndPublishAtLessThanEqual(ContentStatus.SCHEDULED, now);
        dueCollections.forEach(collection -> collection.setContentStatus(ContentStatus.PUBLISHED));
        collectionRepository.saveAll(dueCollections);
    }
}
