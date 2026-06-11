package com.gallery.fineart.mfineart.dto;

import com.gallery.fineart.mfineart.enumeration.EventType;
import com.gallery.fineart.mfineart.model.Image;

import java.time.LocalDate;
import java.util.*;

public class EventDto implements Comparable<EventDto> {

    private Long id;
    private String name;
    private EventType eventType;
    private String description;
    private LocalDate date;
    private Set<Image> images;

    public EventDto() {
        images = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @Override
    public int compareTo(EventDto o) {
        return this.getName().compareTo(o.getName());
    }
}
