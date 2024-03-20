package com.mountainspring.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = "SELECT e.id AS id, e.name AS name, e.date AS date, e.description_id AS descriptionId, IF(e.coordinates IS NULL, 'false', 'true') AS coordinates from event as e", nativeQuery = true)
    List<EventProjection> getAllPreview();

    @Query(value = "SELECT\n" +
            "    e.id AS id,\n" +
            "    e.name AS name,\n" +
            "    e.date AS date,\n" +
            "    e.description_id AS descriptionId,\n" +
            "    IF(e.coordinates IS NULL, 'false', 'true') AS coordinates,\n" +
            "    IF(s3o.file_type = 'video', NULL,\n" +
            "       CONCAT('https://', s3o.bucket_name, '.s3.', s3o.region, '.amazonaws.com/', s3o.path)) AS path\n" +
            "FROM\n" +
            "    event e\n" +
            "        LEFT JOIN (\n" +
            "        SELECT\n" +
            "            event_id,\n" +
            "            s3_object_id,\n" +
            "            ROW_NUMBER() OVER (PARTITION BY event_id ORDER BY sort_order) AS row_num\n" +
            "        FROM\n" +
            "            event_media\n" +
            "    ) em_ranked ON e.id = em_ranked.event_id AND em_ranked.row_num = 1\n" +
            "        LEFT JOIN\n" +
            "    s3object s3o ON em_ranked.s3_object_id = s3o.id;", nativeQuery = true)
    List<EventFrontendProjection> getAllFrontend();
    List<Event> findAllByTripId(UUID id);

    @Query(value = "DELETE from EventMapFeatures WHERE EventID = ?1", nativeQuery = true)
    void deleteEventMapFeaturesByEventId(UUID id);
}
