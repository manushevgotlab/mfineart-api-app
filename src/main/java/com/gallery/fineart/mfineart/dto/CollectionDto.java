package com.gallery.fineart.mfineart.dto;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class CollectionDto implements Comparable<CollectionDto> {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private String thumbnailUrl;
    private Set<Long> paintingIds;

    public CollectionDto() {
        paintingIds = new HashSet<>();
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Set<Long> getPaintingIds() {
        return paintingIds;
    }

    public void setPaintingIds(Set<Long> paintingIds) {
        this.paintingIds = paintingIds;
    }

    @Override
    public int compareTo(CollectionDto o) {
        return this.getName().compareTo(o.getName());
    }
}
