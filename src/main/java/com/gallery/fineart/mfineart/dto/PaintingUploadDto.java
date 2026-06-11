package com.gallery.fineart.mfineart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class PaintingUploadDto {

    @NotNull
    @Valid
    private PaintingDto paintingDto;
    @NotEmpty
    private Map<MultipartFile, Boolean> imagesFiles;

    public PaintingDto getPaintingDto() {
        return paintingDto;
    }

    public void setPaintingDto(PaintingDto paintingDto) {
        this.paintingDto = paintingDto;
    }

    public Map<MultipartFile, Boolean> getImagesFiles() {
        return imagesFiles;
    }

    public void setImagesFiles(Map<MultipartFile, Boolean> imagesFiles) {
        this.imagesFiles = imagesFiles;
    }
}
