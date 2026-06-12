package com.gallery.fineart.mfineart.dto;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ContentStatusUpdateDto {

    @NotNull
    private Long id;

    @NotNull
    private ContentStatus contentStatus;

    private LocalDateTime publishAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
