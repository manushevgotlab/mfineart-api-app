package com.gallery.fineart.mfineart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class CollectionUploadDto {

    @NotNull
    @Valid
    private CollectionDto collectionDto;
    @NotNull
    private MultipartFile thumbnailFile;

    public CollectionDto getCollectionDto() {
        return collectionDto;
    }

    public void setCollectionDto(CollectionDto collectionDto) {
        this.collectionDto = collectionDto;
    }

    public MultipartFile getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(MultipartFile thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }
}
