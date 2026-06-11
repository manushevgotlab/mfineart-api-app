package com.gallery.fineart.mfineart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.web.multipart.MultipartFile;

public class CollectionUploadDto {

    @NotNull
    @Valid
    private CollectionDto collectionDto;
    @NotEmpty
    private ImmutablePair<MultipartFile, Boolean> imageFile;

    public CollectionDto getCollectionDto() {
        return collectionDto;
    }

    public void setCollectionDto(CollectionDto collectionDto) {
        this.collectionDto = collectionDto;
    }

    public ImmutablePair<MultipartFile, Boolean> getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImmutablePair<MultipartFile, Boolean> imageFile) {
        this.imageFile = imageFile;
    }
}
