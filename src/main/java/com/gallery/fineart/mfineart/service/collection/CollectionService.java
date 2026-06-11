package com.gallery.fineart.mfineart.service.collection;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.model.ArtCollection;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CollectionService {
    List<CollectionDto> getAllCollections(boolean sorted);

    ArtCollection getCollectionById(String id);

    ArtCollection addCollection(@Valid CollectionDto collectionDto);

    ArtCollection addCollection(CollectionDto collectionDto, ImmutablePair<MultipartFile, Boolean> imagesFiles);

    Long editCollection(@Valid CollectionDto collectionDto);

    Boolean deleteCollectionById(String collectionId);
}
