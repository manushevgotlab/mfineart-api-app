package com.gallery.fineart.mfineart.model;

import com.gallery.fineart.mfineart.enumeration.EventType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "EVENT" )
public class Event extends BaseGalleryEntity {

    @Column(name = "EVENT_TYPE")
    private EventType eventType;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "EVENT", cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();

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

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}
