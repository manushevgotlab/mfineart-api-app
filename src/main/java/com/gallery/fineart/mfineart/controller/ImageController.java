package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.ImageDto;
import com.gallery.fineart.mfineart.dto.ImageUploadDto;
import com.gallery.fineart.mfineart.service.image.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
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

    @GetMapping("/{id}")
    public ResponseEntity<ImageDto> getImageById(@PathVariable String id) {
        ImageDto imageDto = imageService.getImageById(id);

        return ResponseEntity.ok(imageDto);
    }

    @GetMapping("/by-prefix")
    public ResponseEntity<List<ImageDto>> getImagesByPrefix(@RequestParam String prefix,
                                                            @RequestParam(defaultValue = "true") boolean sorted) {
        List<ImageDto> imageDtos = imageService.getImagesByPrefix(prefix, sorted);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping("/painting/{paintingId}")
    public ResponseEntity<List<ImageDto>> getImagesForPainting(@PathVariable String paintingId) {
        List<ImageDto> imageDtos = imageService.getImagesForPainting(paintingId);

        return ResponseEntity.ok(imageDtos);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ImageDto>> getImagesForEvent(@PathVariable String eventId) {
        List<ImageDto> imageDtos = imageService.getImagesForEvent(eventId);

        return ResponseEntity.ok(imageDtos);
    }

    @PostMapping
    public ResponseEntity<String> createImage(@RequestBody ImageUploadDto imageUploadDto) {
        String url = imageService.addImage(imageUploadDto);

        return ResponseEntity.ok(url);
    }

    @PutMapping("/{imageId}/thumbnail")
    public ResponseEntity<Boolean> updateImageThumbnail(@PathVariable String imageId,
                                                        @RequestParam Boolean isThumbnail) {
        Boolean result = imageService.updateImageThumbnail(imageId, isThumbnail);

        return ResponseEntity.ok(result);
    }
}
