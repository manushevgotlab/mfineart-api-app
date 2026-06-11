package com.gallery.fineart.mfineart.mapper;


import com.gallery.fineart.mfineart.dto.PaintingDto;
import com.gallery.fineart.mfineart.exception.collection.CollectionNotFoundException;
import com.gallery.fineart.mfineart.model.ArtCollection;
import com.gallery.fineart.mfineart.model.Painting;
import com.gallery.fineart.mfineart.repository.CollectionRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PaintingMapper {

    private final CollectionRepository collectionRepository;

    @Autowired
    public PaintingMapper(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Mapping(target = "collection", source = "java(fetchCollection(paintingDto.getCollectionId()))")
    public abstract Painting toPainting(PaintingDto paintingDto);

    @Mapping(target = "collection", source = "java(paintingDto.getCollection().getId())")
    public abstract PaintingDto toPaintingDto(Painting painting);

    private ArtCollection fetchCollection(Long collectionId) {
        try {
            return collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new CollectionNotFoundException(String.valueOf(collectionId)));
        } catch (CollectionNotFoundException e) {
            return null;
        }
    }
}
