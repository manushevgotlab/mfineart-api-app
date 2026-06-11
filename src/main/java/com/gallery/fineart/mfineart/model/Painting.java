package com.gallery.fineart.mfineart.model;

import com.gallery.fineart.mfineart.enumeration.Availability;
import com.gallery.fineart.mfineart.enumeration.Material;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "PAINTING" )
public class Painting extends Artwork {

    @Column(name = "MATERIAL")
    private Material material;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "AVAILABILITY")
    private Availability availability;

    @Column(name = "IN_COLLECTION")
    private Boolean inCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLLECTION_ID")
    private ArtCollection artCollection;

    @OneToMany(mappedBy = "painting", cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();

    @Column(name = "WIDTH")
    private Double width;

    @Column(name = "HEIGHT")
    private Double height;

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability available) {
        this.availability = available;
    }

    public Boolean getInCollection() {
        return inCollection;
    }

    public void setInCollection(Boolean inCollection) {
        this.inCollection = inCollection;
    }

    public ArtCollection getCollection() {
        return artCollection;
    }

    public void setCollection(ArtCollection artCollection) {
        this.artCollection = artCollection;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
