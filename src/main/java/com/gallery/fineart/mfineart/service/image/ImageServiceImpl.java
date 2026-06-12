package com.gallery.fineart.mfineart.service.image;

import com.gallery.fineart.mfineart.dto.*;
import com.gallery.fineart.mfineart.exception.event.EventNotFoundException;
import com.gallery.fineart.mfineart.exception.image.ImageNotFoundException;
import com.gallery.fineart.mfineart.exception.image.MissingEntityBoundForImage;
import com.gallery.fineart.mfineart.exception.painting.PaintingNotFoundException;
import com.gallery.fineart.mfineart.mapper.ImageMapper;
import com.gallery.fineart.mfineart.model.*;
import com.gallery.fineart.mfineart.repository.EventRepository;
import com.gallery.fineart.mfineart.repository.ImageRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
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
    private final PaintingRepository paintingRepository;
    private final EventRepository eventRepository;
    private final S3Service s3Service;

    @Autowired
    public ImageServiceImpl(ImageMapper imageMapper,
                            ImageRepository imageRepository,
                            PaintingRepository paintingRepository,
                            EventRepository eventRepository,
                            S3Service s3Service) {
        this.imageMapper = imageMapper;
        this.imageRepository = imageRepository;
        this.paintingRepository = paintingRepository;
        this.eventRepository = eventRepository;
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

        if (baseGalleryEntity instanceof Painting painting) {
            image.setPainting(painting);
        } else if (baseGalleryEntity instanceof Event event) {
            image.setEvent(event);
        } else {
            throw new IllegalArgumentException("Images can only be bound to paintings or events");
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

        Painting painting = findPaintingById(paintingId);

        return painting.getImages()
                .stream()
                .map(imageMapper::toImageDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageDto> getImagesForEvent(String eventId) {
        if (StringUtils.isEmpty(eventId)) {
            throw new IllegalArgumentException("Parameter eventId cannot be null");
        }

        Event event = findEventById(eventId);

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

        BaseGalleryEntity baseGalleryEntity = resolveImageParentEntity(imageUploadDto);
        return addImageForEntity(imageUploadDto.getImageFile(), imageUploadDto.getThumbnail(), baseGalleryEntity);
    }

    @Override
    public Boolean updateImageThumbnail(String imageId, Boolean isThumbnail) {
        if (StringUtils.isEmpty(imageId)) {
            throw new IllegalArgumentException("Parameter imageId cannot be null");
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

    private BaseGalleryEntity resolveImageParentEntity(ImageUploadDto imageUploadDto) {
        if (Objects.nonNull(imageUploadDto.getPaintingId())) {
            return findPaintingById(String.valueOf(imageUploadDto.getPaintingId()));
        }
        if (Objects.nonNull(imageUploadDto.getEventId())) {
            return findEventById(String.valueOf(imageUploadDto.getEventId()));
        }
        throw new IllegalArgumentException("Image must be bound to a painting or event");
    }

    private Painting findPaintingById(String paintingId) {
        return paintingRepository.findById(Long.parseLong(paintingId))
                .orElseThrow(() -> new PaintingNotFoundException(paintingId));
    }

    private Event findEventById(String eventId) {
        return eventRepository.findById(Long.parseLong(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private void editOldThumbnailForEntity(Image image) {
        Optional<Image> currentThumbnailOpt = Optional.empty();

        if (Objects.nonNull(image.getPainting())) {
            currentThumbnailOpt = image.getPainting().getImages().stream()
                    .filter(Image::getThumbnail)
                    .findFirst();
        } else if (Objects.nonNull(image.getEvent())) {
            currentThumbnailOpt = image.getEvent().getImages().stream()
                    .filter(Image::getThumbnail)
                    .findFirst();
        } else {
            throw new MissingEntityBoundForImage(image.getId());
        }

        if (currentThumbnailOpt.isPresent()) {
            Image currentThumbnail = currentThumbnailOpt.get();
            currentThumbnail.setThumbnail(false);
            imageRepository.save(currentThumbnail);
        }
    }
}
