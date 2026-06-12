package com.gallery.fineart.mfineart.model;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class PublishableEntity extends BaseGalleryEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "CONTENT_STATUS", nullable = false)
    private ContentStatus contentStatus = ContentStatus.DRAFT;

    @Column(name = "PUBLISH_AT")
    private LocalDateTime publishAt;

    public ContentStatus getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(ContentStatus contentStatus) {
        this.contentStatus = contentStatus;
    }

    public LocalDateTime getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(LocalDateTime publishAt) {
        this.publishAt = publishAt;
    }
}
