package com.mountainspring.event;

import com.mountainspring.aws.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = "SELECT e.id AS id, e.name AS name, e.date AS date, IF(e.coordinates IS NULL, 'false', 'true') AS coordinates from event as e", nativeQuery = true)
    List<EventProjection> getAllPreview();
}
