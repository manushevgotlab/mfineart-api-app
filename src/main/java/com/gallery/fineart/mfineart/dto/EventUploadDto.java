package com.gallery.fineart.mfineart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class EventUploadDto {

    @NotNull
    @Valid
    private EventDto eventDto;

    @NotEmpty
    private Map<MultipartFile, Boolean> imagesFiles;

    public EventDto getEventDto() {
        return eventDto;
    }

    public void setEventDto(EventDto eventDto) {
        this.eventDto = eventDto;
    }

    public Map<MultipartFile, Boolean> getImagesFiles() {
        return imagesFiles;
    }

    public void setImagesFiles(Map<MultipartFile, Boolean> imagesFiles) {
        this.imagesFiles = imagesFiles;
    }
}
