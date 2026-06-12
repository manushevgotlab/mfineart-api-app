package com.gallery.fineart.mfineart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "COLLECTION")
public class ArtCollection extends PublishableEntity {

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "THUMBNAIL_URL")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "artCollection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Painting> paintings = new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Set<Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(Set<Painting> paintings) {
        this.paintings = paintings;
    }

    public void addPainting(Painting painting) {
        this.paintings.add(painting);
    }
}
