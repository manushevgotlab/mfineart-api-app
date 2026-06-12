package com.gallery.fineart.mfineart.service.collection;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.dto.ContentStatusUpdateDto;
import com.gallery.fineart.mfineart.model.ArtCollection;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CollectionService {
    List<CollectionDto> getAllCollections(boolean sorted);

    CollectionDto getCollectionById(String id);

    ArtCollection addCollection(@Valid CollectionDto collectionDto);

    ArtCollection addCollection(CollectionDto collectionDto, MultipartFile thumbnailFile);

    Long editCollection(@Valid CollectionDto collectionDto);

    ContentStatusUpdateDto updateContentStatus(ContentStatusUpdateDto contentStatusUpdateDto);

    Boolean deleteCollectionById(String collectionId);
}
