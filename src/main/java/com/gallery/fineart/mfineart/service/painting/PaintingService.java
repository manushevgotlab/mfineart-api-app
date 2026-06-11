package com.gallery.fineart.mfineart.service.painting;

import com.gallery.fineart.mfineart.dto.PaintingDto;
import com.gallery.fineart.mfineart.model.Painting;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface PaintingService {

    boolean isExistingPainting(String paintingId);

    List<PaintingDto> getAllPaintings(boolean sorted);

    PaintingDto getPaintingById(String id);

    Painting findPaintingById(String id);

    List<PaintingDto> getAllPaintingsForCollection(boolean sorted, String collectionId);

    Painting addPainting(PaintingDto paintingDto);

    Painting addPainting(PaintingDto paintingDto, Map<MultipartFile, Boolean> imagesFiles);

    Long editPainting(PaintingDto paintingDto);

    String updatePaintingStatus(String paintingId, String status, Double price);

    Boolean addPaintingToCollection(String paintingId, String collectionId);

    Boolean removePaintingFromCollection(String paintingId);

    /**
     * Images related to the painting are deleted automatically due to "ON DELETE CASCADE" in constraint
     */
    Boolean deletePaintingById(String paintingId);
}
