package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.ImageDto;
import com.gallery.fineart.mfineart.dto.ImageUploadDto;
import com.gallery.fineart.mfineart.service.image.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> getAllImages(@RequestParam(defaultValue = "true") boolean sorted) {
        List<ImageDto> imageDtos = imageService.getAllImages(sorted);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping
    public ResponseEntity<ImageDto> getImageById(@RequestParam String id) {
        ImageDto imageDtos = imageService.getImageById(id);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> getImagesByPrefix(@RequestParam String prefix,
                                                            @RequestParam(defaultValue = "true") boolean sorted) {
        List<ImageDto> imageDtos = imageService.getImagesByPrefix(prefix, sorted);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> getImagesForPainting(@RequestParam String paintingId) {
        List<ImageDto> imageDtos = imageService.getImagesForPainting(paintingId);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping
    public ResponseEntity<List<ImageDto>> getImagesForEvent(@RequestParam String eventId) {
        List<ImageDto> imageDtos = imageService.getImagesForEvent(eventId);

        return ResponseEntity.ok(imageDtos);
    }

    @PostMapping
    public ResponseEntity<String> createImage(@RequestBody ImageUploadDto imageUploadDto) {
        String url = imageService.addImage(imageUploadDto);

        return ResponseEntity.ok(url);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateImageThumbnail(@RequestParam String imageId, @RequestParam Boolean isThumbnail) {
        Boolean result = imageService.updateImageThumbnail(imageId, isThumbnail);

        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<String> bindImagesToPainting(@RequestParam boolean paintingId) {
        return ResponseEntity.ok().build();
    }

}
