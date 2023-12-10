package com.mountainspring.mapFeature;

import com.mountainspring.aws.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MapFeatureRepository extends JpaRepository<MapFeature, UUID> {

    @Query(value = "SELECT m.id AS id, m.name AS name, m.pronunciation AS pronunciation, m.translation AS translation, m.type as type, IF(m.coordinate IS NULL, 'false', 'true') AS coordinate, IF(m.primary_image_id IS NULL, 'false', 'true') AS primaryImage from map_feature as m", nativeQuery = true)
    List<MapFeatureProjection> getAllPreview();

    @Query(value = "SELECT\n" +
            "    mf.*,\n" +
            "    so.id AS image_id, so.path, so.description,\n" +
            "    JSON_ARRAYAGG(\n" +
            "            JSON_OBJECT(\n" +
            "                    'id', e.id,\n" +
            "                    'name', e.name,\n" +
            "                    'trip_id', e.trip_id,\n" +
            "                    'trip_name', t.name,\n" +
            "                    'date', e.date\n" +
            "                )\n" +
            "        ) AS events\n" +
            "FROM\n" +
            "    map_feature mf\n" +
            "        LEFT JOIN\n" +
            "    event_map_features emf ON mf.id = emf.map_features_id\n" +
            "        LEFT JOIN\n" +
            "    event e ON emf.event_id = e.id\n" +
            "        LEFT JOIN\n" +
            "    s3object so ON mf.primary_image_id = so.id\n" +
            "        LEFT JOIN\n" +
            "    trip t ON e.trip_id = t.id\n" +
            "GROUP BY\n" +
            "    mf.id, mf.name, so.id, so.path, so.description;", nativeQuery = true)
    List<Map<String, Object>> findAllDetailed();

    List<MapFeature> findAll();

    List<MapFeature> findByPrimaryImage(S3Object s3Object);
}
