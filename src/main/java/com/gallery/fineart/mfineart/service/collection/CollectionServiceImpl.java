package com.gallery.fineart.mfineart.service.collection;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.dto.ContentStatusUpdateDto;
import com.gallery.fineart.mfineart.exception.collection.CollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.collection.CollectionWithNameAlreadyExists;
import com.gallery.fineart.mfineart.exception.image.InvalidNumberOfImagesForCollection;
import com.gallery.fineart.mfineart.mapper.CollectionMapper;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import com.gallery.fineart.mfineart.service.content.ContentLifecycleService;
import com.gallery.fineart.mfineart.service.content.PublicContentAccessService;
import com.gallery.fineart.mfineart.service.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectionServiceImpl implements CollectionService {

    private static final int EXACT_NUMBER_OF_IMAGES_FOR_COLLECTION = 1;
    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;
    private final S3Service s3Service;
    private final PublicContentAccessService publicContentAccessService;
    private final ContentLifecycleService contentLifecycleService;

    @Autowired
    public CollectionServiceImpl(CollectionRepository collectionRepository,
                                   CollectionMapper collectionMapper,
                                   S3Service s3Service,
                                   PublicContentAccessService publicContentAccessService,
                                   ContentLifecycleService contentLifecycleService) {
        this.collectionRepository = collectionRepository;
        this.collectionMapper = collectionMapper;
        this.s3Service = s3Service;
        this.publicContentAccessService = publicContentAccessService;
        this.contentLifecycleService = contentLifecycleService;
    }

    @Override
    public List<CollectionDto> getAllCollections(boolean sorted) {
        return collectionRepository.findAll()
                .stream()
                .filter(this::isVisibleToCaller)
                .map(collectionMapper::toCollectionDto)
                .sorted(sorted ? Comparator.comparing(CollectionDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public CollectionDto getCollectionById(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Parameter ID cannot be null");
        }

        Optional<ArtCollection> collectionOptional = collectionRepository.findById(Long.parseLong(id));

        if (collectionOptional.isEmpty()) {
            throw new CollectionNotFoundException(id);
        }

        ArtCollection artCollection = collectionOptional.get();
        requireVisibleToCaller(artCollection, id);
        return collectionMapper.toCollectionDto(artCollection);
    }

    @Override
    public ArtCollection addCollection(CollectionDto collectionDto) {
        validateCollectionDto(collectionDto);

        ArtCollection artCollection = collectionMapper.toCollection(collectionDto);
        collectionRepository.save(artCollection);

        return artCollection;
    }

    @Override
    public ArtCollection addCollection(CollectionDto collectionDto, MultipartFile thumbnailFile) {
        if (Objects.isNull(thumbnailFile) || thumbnailFile.isEmpty()) {
            throw new InvalidNumberOfImagesForCollection(EXACT_NUMBER_OF_IMAGES_FOR_COLLECTION, 0);
        }

        ArtCollection artCollection = addCollection(collectionDto);

        try {
            String thumbnailUrl = s3Service.uploadFile(thumbnailFile);
            artCollection.setThumbnailUrl(thumbnailUrl);
            collectionRepository.save(artCollection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return artCollection;
    }

    @Override
    public ContentStatusUpdateDto updateContentStatus(ContentStatusUpdateDto contentStatusUpdateDto) {
        if (Objects.isNull(contentStatusUpdateDto) || Objects.isNull(contentStatusUpdateDto.getId())) {
            throw new IllegalArgumentException("Content status update requires an id");
        }

        ArtCollection artCollection = collectionRepository.findById(contentStatusUpdateDto.getId())
                .orElseThrow(() -> new CollectionNotFoundException(String.valueOf(contentStatusUpdateDto.getId())));
        contentLifecycleService.applyStatus(
                artCollection,
                contentStatusUpdateDto.getContentStatus(),
                contentStatusUpdateDto.getPublishAt());
        collectionRepository.save(artCollection);

        ContentStatusUpdateDto response = new ContentStatusUpdateDto();
        response.setId(artCollection.getId());
        response.setContentStatus(artCollection.getContentStatus());
        response.setPublishAt(artCollection.getPublishAt());
        return response;
    }

    @Override
    public Long editCollection(CollectionDto collectionDto) {
        validateCollectionDto(collectionDto);

        Optional<ArtCollection> optionalCollection = collectionRepository.findById(collectionDto.getId());
        if (optionalCollection.isEmpty()) {
            throw new CollectionNotFoundException(String.valueOf(collectionDto.getId()));
        }

        ArtCollection artCollection = optionalCollection.get();
        artCollection.setName(collectionDto.getName());
        artCollection.setDescription(collectionDto.getDescription());
        if (Objects.nonNull(collectionDto.getDate())) {
            artCollection.setDate(collectionDto.getDate());
        }
        if (Objects.nonNull(collectionDto.getThumbnailUrl())) {
            artCollection.setThumbnailUrl(collectionDto.getThumbnailUrl());
        }

        collectionRepository.save(artCollection);

        return artCollection.getId();
    }

    @Override
    public Boolean deleteCollectionById(String collectionId) {
        if (StringUtils.isEmpty(collectionId)) {
            throw new IllegalArgumentException("Parameter Id cannot be null.");
        }

        Optional<ArtCollection> optionalCollection = collectionRepository.findById(Long.valueOf(collectionId));
        if (optionalCollection.isEmpty()) {
            throw new CollectionNotFoundException(collectionId);
        }
        ArtCollection artCollection = optionalCollection.get();

        collectionRepository.delete(artCollection);

        return true;
    }

    private void validateCollectionDto(CollectionDto collectionDto) {
        if (Objects.isNull(collectionDto)) {
            throw new IllegalArgumentException("Parameter collectionDto cannot be null");
        }
        validateExistingCollectionWithSameName(collectionDto.getName());
    }

    private void validateExistingCollectionWithSameName(String name) {
        Optional<ArtCollection> optionalCollection = collectionRepository.findByName(name);
        if (optionalCollection.isPresent()) {
            throw new CollectionWithNameAlreadyExists(name);
        }
    }

    private boolean isVisibleToCaller(ArtCollection artCollection) {
        return publicContentAccessService.isStaffUser() || publicContentAccessService.isPubliclyVisible(artCollection);
    }

    private void requireVisibleToCaller(ArtCollection artCollection, String id) {
        if (!isVisibleToCaller(artCollection)) {
            throw new CollectionNotFoundException(id);
        }
    }

}
