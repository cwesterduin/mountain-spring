package com.mountainspring.trip;

import com.mountainspring.aws.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query(value = """
            SELECT t1.name AS name,t1.id AS id,t2.date AS date
            FROM trip t1
            LEFT JOIN (SELECT 
                    e.trip_id,
                    MAX(e.date) AS date
                FROM event e
                GROUP BY e.trip_id
            ) t2 ON t1.id = t2.trip_id
            ORDER BY t2.date
            """, nativeQuery = true)
    List<TripProjection> getAllPreview();

    List<Trip> findByPrimaryImage(S3Object s3Object);

}
