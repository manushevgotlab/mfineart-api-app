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

    @Autowired
    private CollectionRepository collectionRepository;

    @Mapping(target = "collection", expression = "java(fetchCollection(paintingDto.getCollectionId()))")
    @Mapping(target = "images", ignore = true)
    public abstract Painting toPainting(PaintingDto paintingDto);

    @Mapping(target = "collectionId", expression = "java(painting.getCollection() != null ? painting.getCollection().getId() : null)")
    public abstract PaintingDto toPaintingDto(Painting painting);

    protected ArtCollection fetchCollection(Long collectionId) {
        if (collectionId == null) {
            return null;
        }
        try {
            return collectionRepository.findById(collectionId)
                    .orElseThrow(() -> new CollectionNotFoundException(String.valueOf(collectionId)));
        } catch (CollectionNotFoundException e) {
            return null;
        }
    }
}
