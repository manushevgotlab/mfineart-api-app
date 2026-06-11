package com.gallery.fineart.mfineart.service.image;

import com.gallery.fineart.mfineart.dto.*;
import com.gallery.fineart.mfineart.exception.collection.ImagesForCollectionNotFoundException;
import com.gallery.fineart.mfineart.exception.image.ImageNotFoundException;
import com.gallery.fineart.mfineart.exception.image.ImagesForEventNotFoundException;
import com.gallery.fineart.mfineart.exception.image.ImagesForPaintingNotFoundException;
import com.gallery.fineart.mfineart.exception.image.MissingEntityBoundForImage;
import com.gallery.fineart.mfineart.mapper.ImageMapper;
import com.gallery.fineart.mfineart.model.*;
import com.gallery.fineart.mfineart.repository.ImageRepository;
import com.gallery.fineart.mfineart.service.collection.CollectionService;
import com.gallery.fineart.mfineart.service.event.EventService;
import com.gallery.fineart.mfineart.service.painting.PaintingService;
import com.gallery.fineart.mfineart.service.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;
    private final PaintingService paintingService;
    private final CollectionService collectionService;
    private final EventService eventService;
    private final S3Service s3Service;

    @Autowired
    public ImageServiceImpl(ImageMapper imageMapper,
                            ImageRepository imageRepository,
                            PaintingService paintingService,
                            CollectionService collectionService,
                            EventService eventService,
                            S3Service s3Service) {
        this.imageMapper = imageMapper;
        this.imageRepository = imageRepository;
        this.paintingService = paintingService;
        this.collectionService = collectionService;
        this.eventService = eventService;
        this.s3Service = s3Service;
    }

    @Override
    public String addImageForEntity(MultipartFile imageFile, Boolean isThumbnail, BaseGalleryEntity baseGalleryEntity) {
        if (Objects.isNull(imageFile) || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is null or empty");
        }

        if (Objects.isNull(baseGalleryEntity)) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String url;
        try {
            url = s3Service.uploadFile(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Image image = new Image();
        image.setName(Paths.get(imageFile.getOriginalFilename()).getFileName().toString());
        image.setUrl(url);
        image.setThumbnail(isThumbnail);
        image.setDate(baseGalleryEntity.getDate());

        if (baseGalleryEntity instanceof ArtCollection) {
            image.setCollection((ArtCollection) baseGalleryEntity);
        } else if (baseGalleryEntity instanceof Painting) {
            image.setPainting((Painting) baseGalleryEntity);
        } else if (baseGalleryEntity instanceof Event) {
            image.setEvent((Event) baseGalleryEntity);
        }

        imageRepository.save(image);

        return url;
    }

    @Override
    public List<ImageDto> getAllImages(boolean sorted) {
        return imageRepository.findAll()
                .stream()
                .map(imageMapper::toImageDto)
                .sorted(sorted ? Comparator.comparing(ImageDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public ImageDto getImageById(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Parameter ID cannot be null");
        }

        Optional<Image> imageOptional = imageRepository.findById(Long.parseLong(id));

        if (imageOptional.isEmpty()) {
            throw new ImageNotFoundException(id);
        }

        return imageMapper.toImageDto(imageOptional.get());
    }

    @Override
    public List<ImageDto> getImagesByPrefix(String prefix, boolean sorted) {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Parameter Prefix cannot be null");
        }

        return imageRepository.findByNameStartingWith(prefix)
                .stream()
                .map(imageMapper::toImageDto)
                .sorted(sorted ? Comparator.comparing(ImageDto::getDate) : Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageDto> getImagesForPainting(String paintingId) {
        if (StringUtils.isEmpty(paintingId)) {
            throw new IllegalArgumentException("Parameter paintingId cannot be null");
        }

        Painting painting = paintingService.getPaintingById(paintingId);

        return painting.getImages()
                .stream()
                .map(imageMapper::toImageDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageDto> getImagesForCollection(String collectionId) {
        if (StringUtils.isEmpty(collectionId)) {
            throw new IllegalArgumentException("Parameter collectionId cannot be null");
        }

        ArtCollection artCollection = collectionService.getCollectionById(collectionId);

        return List.of(imageMapper.toImageDto(artCollection.getThumbnail()));
    }

    @Override
    public List<ImageDto> getImagesForEvent(String eventId) {
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("Parameter eventId cannot be null");
        }

        Event event = eventService.getEventById(eventId);

        return event.getImages()
                .stream()
                .map(imageMapper::toImageDto)
                .collect(Collectors.toList());
    }

    @Override
    public String addImage(ImageUploadDto imageUploadDto) {
        if (Objects.isNull(imageUploadDto)) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        if (Objects.isNull(imageUploadDto.getImageFile())) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        BaseGalleryEntity baseGalleryEntity = validateImageNamePrefix(imageUploadDto);
        String url = addImageForEntity(imageUploadDto.getImageFile(), imageUploadDto.getThumbnail(), baseGalleryEntity);

        return url;
    }

    @Override
    public Boolean updateImageThumbnail(String imageId, Boolean isThumbnail) {
        if (StringUtils.isEmpty(imageId)) {
            throw new IllegalArgumentException("Parameter eventId cannot be null");
        }

        Optional<Image> imageOptional = imageRepository.findById(Long.parseLong(imageId));

        if (imageOptional.isEmpty()) {
            throw new ImageNotFoundException(imageId);
        }

        Image image = imageOptional.get();
        editOldThumbnailForEntity(image);
        image.setThumbnail(isThumbnail);

        imageRepository.save(image);

        return image.getThumbnail();
    }

    private BaseGalleryEntity validateImageNamePrefix(ImageUploadDto imageUploadDto) {
        String fileName = Paths.get(Objects.requireNonNull(imageUploadDto.getImageFile().getOriginalFilename())).getFileName().toString();

        if (Objects.nonNull(imageUploadDto.getPaintingId())) {
            Painting painting = paintingService.getPaintingById(String.valueOf(imageUploadDto.getPaintingId()));
            if (!fileName.startsWith(painting.getName())) {
                throw new ImagesForPaintingNotFoundException(painting.getName(), fileName);
            }
            return painting;
        } else if (Objects.nonNull(imageUploadDto.getCollectionId())) {
            ArtCollection artCollection = collectionService.getCollectionById(String.valueOf(imageUploadDto.getCollectionId()));
            if (!fileName.startsWith(artCollection.getName())) {
                throw new ImagesForCollectionNotFoundException(artCollection.getName(), fileName);
            }
            return artCollection;
        } else if (Objects.nonNull(imageUploadDto.getEventId())) {
            Event event = eventService.getEventById(String.valueOf(imageUploadDto.getEventId()));
            if (!fileName.startsWith(event.getName())) {
                throw new ImagesForEventNotFoundException(event.getName(), fileName);
            }
            return event;
        } else {
            throw new IllegalArgumentException("Image must be bound to an art entity - painting, collection or event");
        }
    }

    private void editOldThumbnailForEntity(Image image) {
        Optional<Image> currentThumbnailOpt = Optional.empty();

        if (Objects.nonNull(image.getPainting())) {
            currentThumbnailOpt = image.getPainting().getImages().stream()
                    .filter(Image::getThumbnail)
                    .findFirst();
        } else if (Objects.nonNull(image.getCollection())) {
            currentThumbnailOpt = Optional.ofNullable(image.getCollection().getThumbnail());
        } else if (Objects.nonNull(image.getEvent())) {
            currentThumbnailOpt = image.getEvent().getImages().stream()
                    .filter(Image::getThumbnail)
                    .findFirst();
        } else {
            throw new MissingEntityBoundForImage(image.getId());
        }

        if(currentThumbnailOpt.isPresent()) {
            Image currentThumbnail = currentThumbnailOpt.get();
            currentThumbnail.setThumbnail(false);
            imageRepository.save(currentThumbnail);
        }
    }
}
