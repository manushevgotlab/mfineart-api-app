package com.gallery.fineart.mfineart.service.content;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.exception.content.InvalidContentStatusTransitionException;
import com.gallery.fineart.mfineart.model.PublishableEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class ContentLifecycleService {

    private static final Map<ContentStatus, Set<ContentStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ContentStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ContentStatus.DRAFT, EnumSet.of(ContentStatus.SCHEDULED, ContentStatus.PUBLISHED, ContentStatus.ARCHIVED));
        ALLOWED_TRANSITIONS.put(ContentStatus.SCHEDULED, EnumSet.of(ContentStatus.DRAFT, ContentStatus.PUBLISHED));
        ALLOWED_TRANSITIONS.put(ContentStatus.PUBLISHED, EnumSet.of(ContentStatus.ARCHIVED));
        ALLOWED_TRANSITIONS.put(ContentStatus.ARCHIVED, EnumSet.of(ContentStatus.DRAFT));
    }

    public void applyStatus(PublishableEntity entity, ContentStatus newStatus, LocalDateTime publishAt) {
        ContentStatus currentStatus = entity.getContentStatus();
        validateTransition(currentStatus, newStatus);

        if (newStatus == ContentStatus.SCHEDULED) {
            if (publishAt == null) {
                throw new IllegalArgumentException("publishAt is required when content status is SCHEDULED");
            }
            entity.setPublishAt(publishAt);
        }

        if (newStatus == ContentStatus.PUBLISHED) {
            if (entity.getPublishAt() == null) {
                entity.setPublishAt(LocalDateTime.now());
            }
        }

        if (newStatus == ContentStatus.DRAFT) {
            entity.setPublishAt(null);
        }

        entity.setContentStatus(newStatus);
    }

    public void validateTransition(ContentStatus from, ContentStatus to) {
        if (from == to) {
            return;
        }
        Set<ContentStatus> allowedTargets = ALLOWED_TRANSITIONS.get(from);
        if (allowedTargets == null || !allowedTargets.contains(to)) {
            throw new InvalidContentStatusTransitionException(from, to);
        }
    }
}
