package com.gallery.fineart.mfineart.repository;

import com.gallery.fineart.mfineart.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByUrl(String url);

    List<Image> findByNameStartingWith(String prefix);
}
