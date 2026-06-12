package com.gallery.fineart.mfineart.repository;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.model.ArtCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<ArtCollection, Long> {

    Optional<ArtCollection> findByName(String name);

    List<ArtCollection> findByContentStatusAndPublishAtLessThanEqual(ContentStatus contentStatus, LocalDateTime publishAt);
}
