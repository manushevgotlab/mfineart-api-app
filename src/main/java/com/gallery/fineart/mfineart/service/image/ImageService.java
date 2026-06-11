package com.gallery.fineart.mfineart.service.image;

import com.gallery.fineart.mfineart.dto.ImageDto;
import com.gallery.fineart.mfineart.dto.ImageUploadDto;
import com.gallery.fineart.mfineart.model.BaseGalleryEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ImageService {
    String addImageForEntity(MultipartFile imageFile, Boolean isThumbnail, BaseGalleryEntity baseEntity);

    List<ImageDto> getAllImages(boolean sorted);

    ImageDto getImageById(String id);

    List<ImageDto> getImagesByPrefix(String prefix, boolean sorted);

    List<ImageDto> getImagesForPainting(String paintingId);

    List<ImageDto> getImagesForCollection(String collectionId);

    List<ImageDto> getImagesForEvent(String eventId);

    String addImage(ImageUploadDto imageDto);

    Boolean updateImageThumbnail(String imageId, Boolean isThumbnail);
}
