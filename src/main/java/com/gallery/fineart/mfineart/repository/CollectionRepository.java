package com.gallery.fineart.mfineart.repository;

import com.gallery.fineart.mfineart.model.ArtCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<ArtCollection, Long> {

    public Optional<ArtCollection> findByName(String name);

}
