package com.gallery.fineart.mfineart.service.collection;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.exception.collection.CollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.collection.CollectionWithNameAlreadyExists;
import com.gallery.fineart.mfineart.exception.collection.ImagesForCollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.image.InvalidImagesThumbnailCountException;
import com.gallery.fineart.mfineart.exception.image.InvalidNumberOfImagesForCollection;
import com.gallery.fineart.mfineart.mapper.CollectionMapper;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import com.gallery.fineart.mfineart.service.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectionServiceImpl implements CollectionService {

    private static final int EXACT_NUMBER_OF_IMAGES_FOR_COLLECTION = 1;
    private static final long EXACT_NUMBER_OF_THUMBNAILS_PER_SET_OF_IMAGES_FOR_COLLECTION = 1;
    private final PaintingRepository paintingRepository;
    private final CollectionRepository collectionRepository;
    private final ImageService imageService;
    private final CollectionMapper collectionMapper;

    @Autowired
    public CollectionServiceImpl(PaintingRepository paintingRepository, CollectionRepository collectionRepository, ImageService imageService, CollectionMapper collectionMapper) {
        this.paintingRepository = paintingRepository;
        this.collectionRepository = collectionRepository;
        this.imageService = imageService;
        this.collectionMapper = collectionMapper;
    }


    @Override
    public List<CollectionDto> getAllCollections(boolean sorted) {
        return collectionRepository.findAll()
                .stream()
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

        return collectionMapper.toCollectionDto(collectionOptional.get());
    }

    @Override
    public ArtCollection addCollection(CollectionDto collectionDto) {
        validateCollectionDto(collectionDto);

        ArtCollection artCollection = collectionMapper.toCollection(collectionDto);
        collectionRepository.save(artCollection);

        return artCollection;
    }

    @Override
    public ArtCollection addCollection(CollectionDto collectionDto, ImmutablePair<MultipartFile, Boolean> imageFile) {
        if (Objects.isNull(imageFile)) {
            throw new InvalidNumberOfImagesForCollection(EXACT_NUMBER_OF_IMAGES_FOR_COLLECTION, 0);
        }

        validateCollectionImagesHaveThumbnail(imageFile.right);
        validateImagesNamePrefixMatchesCollectionName(imageFile.left, collectionDto.getName());

        ArtCollection artCollection = addCollection(collectionDto);

        imageService.addImageForEntity(imageFile.left, imageFile.right, artCollection);

        return artCollection;
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
        if(Objects.nonNull(collectionDto.getDate())) {
            artCollection.setDate(collectionDto.getDate());
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

    private void validateCollectionImagesHaveThumbnail(Boolean imageIsThumbnail) {
        if(Objects.isNull(imageIsThumbnail)) {
            throw new IllegalArgumentException("Parameter imageIsThumbnail cannot be null");
        }

        if(!imageIsThumbnail) {
            throw new InvalidImagesThumbnailCountException(EXACT_NUMBER_OF_IMAGES_FOR_COLLECTION, 0);

        }
    }

    private void validateImagesNamePrefixMatchesCollectionName(MultipartFile imageFile, String collectionName) {
        if(Objects.isNull(imageFile)) {
            throw new IllegalArgumentException("Parameter imageFile cannot be null");
        }

        String fileName = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
        if (!fileName.startsWith(collectionName)) {
            throw new ImagesForCollectionNotFoundException(collectionName, fileName);
        }
    }

}
