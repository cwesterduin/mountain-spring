package com.mountainspring.mapFeature;

import com.mountainspring.aws.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MapFeatureRepository extends JpaRepository<MapFeature, UUID> {

    @Query(value = """
    SELECT 
        m.id AS id, 
        m.name AS name, 
        m.pronunciation AS pronunciation, 
        m.translation AS translation, 
        m.type AS type, 
        m.coordinate AS coordinate, 
        CASE 
            WHEN m.primary_image_id IS NULL THEN 'false' 
            ELSE 'true' 
        END AS primaryImage 
    FROM map_feature AS m
    """, nativeQuery = true)
    List<MapFeatureProjection> getAllPreview();

    @Query(value = """
                        SELECT
                            mf.*,
                            so.id AS image_id, so.path, so.description,
                            jsonb_agg(
                                jsonb_build_object(
                                    'id', CAST(e.id AS text),
                                    'name', e.name,
                                    'trip_id', CAST(e.trip_id AS text),
                                    'trip_name', t.name,
                                    'date', CAST(e.date AS text)
                                )
                            ) AS events
                        FROM
                            map_feature mf
                            LEFT JOIN event_map_features emf ON mf.id = emf.map_features_id
                            LEFT JOIN event e ON emf.event_id = e.id
                            LEFT JOIN s3object so ON mf.primary_image_id = so.id
                            LEFT JOIN trip t ON e.trip_id = t.id
                        GROUP BY
                            mf.id, mf.name, so.id, so.path, so.description;
            """, nativeQuery = true)
    List<Map<String, Object>> findAllDetailed();

    List<MapFeature> findAll();

    List<MapFeature> findByPrimaryImage(S3Object s3Object);
}
