package com.gallery.fineart.mfineart.service.content;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.model.PublishableEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PublicContentAccessService {

    public boolean isStaffUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public boolean isPubliclyVisible(PublishableEntity entity) {
        return isPubliclyVisible(entity, LocalDateTime.now());
    }

    public boolean isPubliclyVisible(PublishableEntity entity, LocalDateTime now) {
        if (entity.getContentStatus() == ContentStatus.PUBLISHED) {
            return true;
        }
        return entity.getContentStatus() == ContentStatus.SCHEDULED
                && entity.getPublishAt() != null
                && !entity.getPublishAt().isAfter(now);
    }
}
