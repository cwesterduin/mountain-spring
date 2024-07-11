package com.mountainspring.vector;

import com.mountainspring.mapFeature.MapFeatureProjection;
import org.springframework.ai.embedding.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface EventVectorRepository extends JpaRepository<EventVector, Long> {

    @Query(value = "SELECT\n" +
            "    CAST(e.id AS text) AS id,\n" +
            "    e.description AS description,\n" +
            "    e.name AS name,\n" +
            "   e.date AS date,\n" +
            "    CAST(e.description_id AS text) AS descriptionId,\n" +
            "    CASE WHEN e.coordinates IS NULL THEN 'false' ELSE 'true' END AS coordinates,\n" +
            "    CASE WHEN s3o.file_type = 'video' THEN NULL\n" +
            "         ELSE CONCAT('https://', s3o.bucket_name, '.s3.', s3o.region, '.amazonaws.com/', s3o.path) END AS path\n" +
            "    FROM event_vector \n" +
            "        JOIN event e on event_vector.event_id = e.id \n" +
            "        LEFT JOIN (\n" +
            "        SELECT\n" +
            "            event_id,\n" +
            "            s3_object_id,\n" +
            "            ROW_NUMBER() OVER (PARTITION BY event_id ORDER BY sort_order) AS row_num\n" +
            "        FROM\n" +
            "            event_media\n" +
            "    ) em_ranked ON e.id = em_ranked.event_id AND em_ranked.row_num = 1\n" +
            "        LEFT JOIN\n" +
            "    s3object s3o ON em_ranked.s3_object_id = s3o.id" +
            "   ORDER BY embedding <-> CAST(?1 AS vector) LIMIT 5\n" +
            ";", nativeQuery = true)
    List<Map<String, Object>> search(String embedding);


}
