package com.gallery.fineart.mfineart.mapper;

import com.gallery.fineart.mfineart.dto.CollectionDto;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.PaintingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    @Autowired
    private PaintingRepository paintingRepository;

    @Mapping(target = "paintings", expression = "java(fetchPaintings(collectionDto.getPaintingIds()))")
    @Mapping(target = "contentStatus", ignore = true)
    @Mapping(target = "publishAt", ignore = true)
    public abstract ArtCollection toCollection(CollectionDto collectionDto);

    @Mapping(target = "paintingIds", expression = "java(getPaintingIds(artCollection.getPaintings()))")
    public abstract CollectionDto toCollectionDto(ArtCollection artCollection);

    protected Set<Painting> fetchPaintings(Set<Long> paintingIds) {
        if (paintingIds == null) {
            return Set.of();
        }
        return paintingIds.stream()
                .map(paintingRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    protected Set<Long> getPaintingIds(Set<Painting> paintings) {
        if (paintings == null) {
            return Set.of();
        }
        return paintings.stream()
                .map(Painting::getId)
                .collect(Collectors.toSet());
    }
}
