package com.mountainspring.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query(value = "SELECT e.id AS id, e.name AS name, e.date AS date, e.description_id AS descriptionId, CASE WHEN e.coordinates IS NULL THEN 'false' ELSE 'true' END AS coordinates from event as e", nativeQuery = true)
    List<EventProjection> getAllPreview();

    @Query(value = """
    SELECT
        CAST(e.id AS text) AS id,
        e.name AS name,
        e.date AS date,
        CAST(e.description_id AS text) AS descriptionId,
        CASE WHEN e.coordinates IS NULL THEN 'false' ELSE 'true' END AS coordinates,
        CASE WHEN s3o.file_type = 'video' THEN NULL
             ELSE CONCAT('https://', s3o.bucket_name, '.s3.', s3o.region, '.amazonaws.com/', s3o.path) END AS path
    FROM
        event e
            LEFT JOIN (
            SELECT
                event_id,
                s3_object_id,
                ROW_NUMBER() OVER (PARTITION BY event_id ORDER BY sort_order) AS row_num
            FROM
                event_media
        ) em_ranked ON e.id = em_ranked.event_id AND em_ranked.row_num = 1
            LEFT JOIN
        s3object s3o ON em_ranked.s3_object_id = s3o.id;
    """, nativeQuery = true)
    List<EventFrontendProjection> getAllFrontend();

    List<Event> findAllByTripId(UUID id);

    @Query(value = "DELETE from event_map_features WHERE event_id = ?1", nativeQuery = true)
    void deleteEventMapFeaturesByEventId(UUID id);

    public List<Event> findAll();
}
