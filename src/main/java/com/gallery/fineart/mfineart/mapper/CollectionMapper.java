package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.exception.image.ImageNotFoundException;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Image;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.ImageRepository;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    private final PaintingRepository paintingRepository;

    private final ImageRepository imageRepository;

    @Autowired
    protected CollectionMapper(PaintingRepository paintingRepository, ImageRepository imageRepository) {
        this.paintingRepository = paintingRepository;
        this.imageRepository = imageRepository;
    }

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thumbnail", source = "java(fetchImage(collectionDto.getThumbnailUrl()))")
    @Mapping(target = "paintings", source = "java(fetchPaintings(collectionDto.getPaintingIds()))")
    public abstract ArtCollection toCollection(CollectionDto collectionDto);

    @Mapping(target = "paintingIds", source = "java(getPaintingIds(collectionDto.getPaintings()))")
    public abstract CollectionDto toCollectionDto(ArtCollection artCollection);

    private Set<Painting> fetchPaintings(Set<Long> paintingIds) {
        return paintingIds.stream()
                .map(paintingRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<Long> getPaintingIds(Set<Painting> paintings) {
        return paintings.stream()
                .map(Painting::getId)
                .collect(Collectors.toSet());
    }

    private Image fetchImage(String thumbnailUrl) {
        try {
            return imageRepository.findByUrl(thumbnailUrl)
                    .orElseThrow(() -> new ImageNotFoundException(thumbnailUrl));
        } catch (ImageNotFoundException e) {
            return null;
        }
    }
}
