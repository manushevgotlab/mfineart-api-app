package com.gallery.fineart.mfineart.repository;

import com.gallery.fineart.mfineart.model.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    List<Painting> findAllByArtCollection_Id(Long collectionId);
}
