package com.mountainspring.mapFeature;

import com.mountainspring.event.EventProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MapFeatureRepository extends JpaRepository<MapFeature, UUID> {

    @Query(value = "SELECT m.id AS id, m.name AS name, m.pronunciation AS pronunciation, m.translation AS translation, m.type as type, IF(m.coordinate IS NULL, 'false', 'true') AS coordinate, IF(m.primary_image_id IS NULL, 'false', 'true') AS primaryImage from map_feature as m", nativeQuery = true)
    List<MapFeatureProjection> getAllPreview();

}
