package com.gallery.fineart.mfineart.dto;

import com.gallery.fineart.mfineart.enumeration.Availability;
import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.enumeration.Material;
import com.gallery.fineart.mfineart.model.Image;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class PaintingDto implements Comparable<PaintingDto> {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private Material material;
    private String description;
    private Double price;
    @NotNull
    private Availability availability;
    @NotNull
    private Boolean inCollection;
    private Long collectionId;
    @NotEmpty
    private Set<Image> images;
    @NotNull
    private Double width;
    @NotNull
    private Double height;
    @NotNull
    private LocalDate date;
    private ContentStatus contentStatus;
    private LocalDateTime publishAt;

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

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Boolean getInCollection() {
        return inCollection;
    }

    public void setInCollection(Boolean inCollection) {
        this.inCollection = inCollection;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    @Override
    public int compareTo(PaintingDto p) {
        return this.getName().compareTo(p.getName());
    }
}
