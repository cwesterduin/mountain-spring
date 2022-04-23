package com.mountainspring.trip;

import com.mountainspring.event.EventProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query(value = "SELECT t1.name as name, t1.id as id, t2.date as date \n" +
            "FROM trip t1\n" +
            "         INNER JOIN\n" +
            "     (\n" +
            "         SELECT name, date, trip_id\n" +
            "         FROM event\n" +
            "         GROUP BY trip_id\n" +
            "     ) as t2\n" +
            "     ON t1.id = t2.trip_id\n" +
            "ORDER BY date\n"
            , nativeQuery = true)
    List<TripProjection> getAllPreview();

}
