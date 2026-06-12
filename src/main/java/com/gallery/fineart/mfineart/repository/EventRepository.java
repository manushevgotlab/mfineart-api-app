package com.gallery.fineart.mfineart.repository;

import com.gallery.fineart.mfineart.enumeration.ContentStatus;
import com.gallery.fineart.mfineart.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByContentStatusAndPublishAtLessThanEqual(ContentStatus contentStatus, LocalDateTime publishAt);
}
