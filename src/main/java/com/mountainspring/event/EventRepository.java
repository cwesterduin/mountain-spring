package com.mountainspring.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = "SELECT e.id AS id, e.name AS name, e.date AS date, e.description_id AS descriptionId, IF(e.coordinates IS NULL, 'false', 'true') AS coordinates from event as e", nativeQuery = true)
    List<EventProjection> getAllPreview();

    @Query(value = "select e.id AS id, e.name AS name, e.date AS date, e.description_id AS descriptionId, IF(e.coordinates IS NULL, 'false', 'true') AS coordinates, " +
            "CONCAT('https://', s3o.bucket_name, '.s3.', s3o.region, '.amazonaws.com/', s3o.path) as path, " +
            "MIN(em.sort_order) as sort_order from event e\n" +
            "join event_media em on e.id = em.event_id\n" +
            "join s3object s3o on em.s3_object_id = s3o.id\n" +
            "GROUP BY event_id;", nativeQuery = true)
    List<EventFrontendProjection> getAllFrontend();
    List<Event> findAllByTripId(UUID id);

    @Query(value = "DELETE from EventMapFeatures WHERE EventID = ?1", nativeQuery = true)
    void deleteEventMapFeaturesByEventId(UUID id);
}
