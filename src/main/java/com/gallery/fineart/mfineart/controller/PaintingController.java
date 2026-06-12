package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.ContentStatusUpdateDto;
import com.gallery.fineart.mfineart.dto.PaintingDto;
import com.gallery.fineart.mfineart.dto.PaintingUploadDto;
import com.gallery.fineart.mfineart.service.painting.PaintingService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/paintings")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public ResponseEntity<List<PaintingDto>> getAllPaintings(
            @RequestParam(defaultValue = "true") boolean sorted) {
        List<PaintingDto> paintings = paintingService.getAllPaintings(sorted);

        return ResponseEntity.ok(paintings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaintingDto> getPaintingById(@PathVariable String id) {
        PaintingDto paintingDto = paintingService.getPaintingById(id);

        return ResponseEntity.ok(paintingDto);
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<List<PaintingDto>> getPaintingsByCollectionId(@RequestParam(defaultValue = "true") boolean sorted,
                                                                        @PathVariable String collectionId) {
        List<PaintingDto> paintings = paintingService.getAllPaintingsForCollection(sorted, collectionId);

        return ResponseEntity.ok(paintings);
    }

    @PostMapping("/painting")
    public ResponseEntity<Long> addPainting(@Valid @RequestBody PaintingDto paintingDto) {
        Long paintingId = paintingService.addPainting(paintingDto).getId();

        return ResponseEntity.ok(paintingId);
    }

    @PostMapping("/painting-images")
    public ResponseEntity<Long> addPaintingWithImages(@Valid @RequestBody PaintingUploadDto dto) {
        Long paintingId = paintingService.addPainting(dto.getPaintingDto(), dto.getImagesFiles()).getId();

        return ResponseEntity.ok(paintingId);
    }

    @PutMapping("/painting/content-status")
    public ResponseEntity<ContentStatusUpdateDto> updateContentStatus(
            @Valid @RequestBody ContentStatusUpdateDto contentStatusUpdateDto) {
        ContentStatusUpdateDto result = paintingService.updateContentStatus(contentStatusUpdateDto);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/painting")
    public ResponseEntity<Long> editPainting(@Valid @RequestBody PaintingDto paintingDto) {
        Long paintingId = paintingService.editPainting(paintingDto);

        return ResponseEntity.ok(paintingId);
    }

    @PutMapping("/painting/status")
    public ResponseEntity<String> updatePaintingStatus(
            @RequestParam String id,
            @RequestParam String status,
            @RequestParam String price) {
        String paintingId = paintingService.updatePaintingStatus(id, status, Double.parseDouble(price));

        return ResponseEntity.ok(paintingId);
    }

    @PutMapping("/painting-in-collection")
    public ResponseEntity<Boolean> addPaintingToCollection(
            @RequestParam String paintingId,
            @RequestParam String collectionId) {
        Boolean isAdded = paintingService.addPaintingToCollection(paintingId, collectionId);

        return ResponseEntity.ok(isAdded);
    }

    @PutMapping("/painting-out-collection")
    public ResponseEntity<Boolean> removePaintingFromCollection(
            @RequestParam String paintingId) {
        Boolean isRemoved = paintingService.removePaintingFromCollection(paintingId);

        return ResponseEntity.ok(isRemoved);
    }

    @DeleteMapping
    public ResponseEntity<String> deletePaintingById(@RequestParam String paintingId) {
        Boolean isDeleted = paintingService.deletePaintingById(paintingId);

        JSONObject result = new JSONObject().put("deleted", isDeleted);

        return ResponseEntity.ok(result.toString());
    }
}
