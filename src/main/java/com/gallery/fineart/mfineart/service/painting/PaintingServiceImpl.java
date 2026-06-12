package com.gallery.fineart.mfineart.service.painting;

import com.gallery.fineart.mfineart.dto.PaintingDto;
import com.gallery.fineart.mfineart.enumeration.Availability;
import com.gallery.fineart.mfineart.exception.collection.CollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.image.InvalidImagesThumbnailCountException;
import com.gallery.fineart.mfineart.exception.painting.PaintingNotFoundException;
import com.gallery.fineart.mfineart.mapper.PaintingMapper;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import com.gallery.fineart.mfineart.service.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaintingServiceImpl implements PaintingService {

    private static final long EXACT_NUMBER_OF_THUMBNAILS_PER_SET_OF_IMAGES = 1L;

    private final PaintingRepository paintingRepository;
    private final CollectionRepository collectionRepository;
    private final PaintingMapper paintingMapper;
    private final ImageService imageService;

    @Autowired
    public PaintingServiceImpl(PaintingRepository paintingRepository,
                               PaintingMapper paintingMapper,
                               CollectionRepository collectionRepository,
                               ImageService imageService) {
        this.paintingRepository = paintingRepository;
        this.paintingMapper = paintingMapper;
        this.collectionRepository = collectionRepository;
        this.imageService = imageService;
    }

    @Override
    public boolean isExistingPainting(String paintingId) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter ID cannot be null");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.parseLong(paintingId));

        return paintingOptional.isPresent();
    }

    @Override
    public List<PaintingDto> getAllPaintings(boolean sorted) {
        return paintingRepository.findAll()
                .stream()
                .map(paintingMapper::toPaintingDto)
                .sorted(sorted ? Comparator.comparing(PaintingDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public PaintingDto getPaintingById(String id) {
        return paintingMapper.toPaintingDto(findPaintingById(id));
    }

    @Override
    public Painting findPaintingById(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Parameter ID cannot be null");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.parseLong(id));

        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(id);
        }

        return paintingOptional.get();
    }

    @Override
    public List<PaintingDto> getAllPaintingsForCollection(boolean sorted, String collectionId) {
        if (StringUtils.isEmpty(collectionId)) {
            throw new IllegalArgumentException("Parameter collectionId cannot be null");
        }

        return paintingRepository.findAllByArtCollection_Id(Long.valueOf(collectionId))
                .stream()
                .map(paintingMapper::toPaintingDto)
                .sorted(sorted ? Comparator.comparing(PaintingDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public Painting addPainting(PaintingDto paintingDto) {
        validatePaintingDto(paintingDto);
        Optional<ArtCollection> optionalCollection = collectionRepository.findById(paintingDto.getCollectionId());

        if (optionalCollection.isEmpty()) {
            throw new CollectionNotFoundException(String.valueOf(paintingDto.getCollectionId()));
        }
        ArtCollection artCollection = optionalCollection.get();

        Painting painting = paintingMapper.toPainting(paintingDto);
        painting.setCollection(artCollection);
        paintingRepository.save(painting);

        return painting;
    }

    @Override
    public Painting addPainting(PaintingDto paintingDto, Map<MultipartFile, Boolean> imagesFiles) {
        if (Objects.isNull(imagesFiles) || imagesFiles.isEmpty()) {
            throw new IllegalArgumentException("Image files cannot be null");
        }

        validateImagesHaveThumbnail(imagesFiles.values());

        Painting painting = addPainting(paintingDto);

        for (Map.Entry<MultipartFile, Boolean> entry : imagesFiles.entrySet()) {
            MultipartFile imageFile = entry.getKey();
            Boolean isThumbnail = entry.getValue();
            imageService.addImageForEntity(imageFile, isThumbnail, painting);
        }

        return painting;
    }

    @Override
    public Long editPainting(PaintingDto paintingDto) {
        validatePaintingDto(paintingDto);
        if (paintingDto.getId() == null) {
            throw new IllegalArgumentException("Parameter Id of PaintingDto cannot be null.");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(paintingDto.getId());
        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(String.valueOf(paintingDto.getId()));
        }

        Painting painting = paintingOptional.get();
        painting.setName(paintingDto.getName());
        painting.setDescription(paintingDto.getDescription());
        painting.setMaterial(paintingDto.getMaterial());
        painting.setAvailability(paintingDto.getAvailability());
        painting.setPrice(paintingDto.getPrice());
        painting.setWidth(paintingDto.getWidth());
        painting.setHeight(paintingDto.getHeight());

        if(Objects.nonNull(paintingDto.getDate())) {
            painting.setDate(paintingDto.getDate());
        }

        if (paintingDto.getInCollection() && paintingDto.getInCollection() != painting.getInCollection()) {
            Optional<ArtCollection> optionalCollection = collectionRepository.findById(paintingDto.getCollectionId());
            if (optionalCollection.isEmpty()) {
                throw new CollectionNotFoundException(String.valueOf(paintingDto.getCollectionId()));
            }
            painting.setInCollection(paintingDto.getInCollection());
            painting.setCollection(optionalCollection.get());
        } else if (!paintingDto.getInCollection()) {
            painting.setInCollection(false);
            painting.setCollection(null);
        }

        paintingRepository.save(painting);

        return painting.getId();
    }

    @Override
    public String updatePaintingStatus(String paintingId, String status, Double price) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter Id cannot be null.");
        }
        if (StringUtils.isEmpty(status)) {
            throw new IllegalArgumentException("Parameter status cannot be null.");
        }
        validatePaintingAvailability(Availability.valueOf(status), price);

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.valueOf(paintingId));
        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(paintingId);
        }

        Painting painting = paintingOptional.get();
        painting.setAvailability(Availability.valueOf(status));
        painting.setPrice(price);

        paintingRepository.save(painting);

        return paintingId;
    }

    @Override
    public Boolean addPaintingToCollection(String paintingId, String collectionId) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter paintingId cannot be null.");
        }
        if (StringUtils.isEmpty(collectionId)) {
            throw new IllegalArgumentException("Parameter collectionId cannot be null.");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.valueOf(paintingId));
        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(paintingId);
        }
        Painting painting = paintingOptional.get();

        Optional<ArtCollection> optionalCollection = collectionRepository.findById(Long.valueOf(collectionId));
        if (optionalCollection.isEmpty()) {
            throw new CollectionNotFoundException(collectionId);
        }
        ArtCollection artCollection = optionalCollection.get();

        painting.setCollection(artCollection);
        paintingRepository.save(painting);

        return true;
    }

    @Override
    public Boolean removePaintingFromCollection(String paintingId) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter paintingId cannot be null.");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.valueOf(paintingId));
        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(paintingId);
        }
        Painting painting = paintingOptional.get();
        painting.setCollection(null);
        paintingRepository.save(painting);

        return true;
    }

    @Override
    public Boolean deletePaintingById(String paintingId) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter Id cannot be null.");
        }

        Optional<Painting> paintingOptional = paintingRepository.findById(Long.valueOf(paintingId));
        if (paintingOptional.isEmpty()) {
            throw new PaintingNotFoundException(paintingId);
        }
        Painting painting = paintingOptional.get();

        paintingRepository.delete(painting);

        return true;
    }

    private void validateImagesHaveThumbnail(Collection<Boolean> imagesThumbnails) {
        long thumbnailsCount = imagesThumbnails.stream()
                .filter(isThumbnail -> isThumbnail)
                .count();

        if (thumbnailsCount != EXACT_NUMBER_OF_THUMBNAILS_PER_SET_OF_IMAGES) {
            throw new InvalidImagesThumbnailCountException(imagesThumbnails.size(), thumbnailsCount);
        }
    }

    private void validatePaintingDto(PaintingDto paintingDto) {
        if (Objects.isNull(paintingDto)) {
            throw new IllegalArgumentException("Parameter PaintingDto cannot be null.");
        }

        validatePaintingAvailability(paintingDto.getAvailability(), paintingDto.getPrice());
        validatePaintingCollectionStatus(paintingDto.getInCollection(), paintingDto.getCollectionId());
    }

    private void validatePaintingCollectionStatus(Boolean inCollection, Long collectionId) {
        if (inCollection && Objects.isNull(collectionId)) {
            throw new IllegalArgumentException("Painting collection ID cannot be null when painting is in collection");
        }
    }

    private void validatePaintingAvailability(Availability availability, Double price) {
        if (availability.equals(Availability.AVAILABLE) && Objects.isNull(price)) {
            throw new IllegalArgumentException("Painting price cannot be null when availability is status available");
        }
    }
}
