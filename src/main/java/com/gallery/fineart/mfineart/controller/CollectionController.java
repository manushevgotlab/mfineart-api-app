package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.dto.CollectionUploadDto;
import com.gallery.fineart.mfineart.service.collection.CollectionService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/collections")
public class CollectionController {

    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<CollectionDto>> getAllCollections(
            @RequestParam(defaultValue = "true") boolean sorted) {
        List<CollectionDto> collections = collectionService.getAllCollections(sorted);

        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionDto> getCollectionById(@PathVariable String id) {
        CollectionDto collectionDto = collectionService.getCollectionById(id);

        return ResponseEntity.ok(collectionDto);
    }

    @PostMapping("/collection")
    public ResponseEntity<Long> addCollection(@Valid @RequestBody CollectionDto collectionDto) {
        Long collectionId = collectionService.addCollection(collectionDto).getId();

        return ResponseEntity.ok(collectionId);
    }

    @PostMapping("/collection-images")
    public ResponseEntity<Long> addCollectionWithImage(@Valid @RequestBody CollectionUploadDto dto) {
        Long collectionId = collectionService.addCollection(dto.getCollectionDto(), dto.getImageFile()).getId();

        return ResponseEntity.ok(collectionId);
    }

    @PutMapping
    public ResponseEntity<Long> editCollection(@Valid @RequestBody CollectionDto collectionDto) {
        Long collectionId = collectionService.editCollection(collectionDto);

        return ResponseEntity.ok(collectionId);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCollectionById(@RequestParam String collectionId) {
        Boolean isDeleted = collectionService.deleteCollectionById(collectionId);

        JSONObject result = new JSONObject().put("deleted", isDeleted);

        return ResponseEntity.ok(result.toString());
    }
}
