package com.mountainspring.trip;

import com.mountainspring.aws.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query(value = "SELECT t1.name as name, t1.id as id, t2.date as date\n" +
            "FROM trip t1\n" +
            "        LEFT JOIN\n" +
            "     (\n" +
            "         SELECT name, date, trip_id\n" +
            "         FROM event\n" +
            "         GROUP BY trip_id\n" +
            "     ) as t2\n" +
            "     ON t1.id = t2.trip_id\n" +
            "ORDER BY date"
            , nativeQuery = true)
    List<TripProjection> getAllPreview();

    List<Trip> findByPrimaryImage(S3Object s3Object);

}
