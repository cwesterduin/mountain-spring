package com.mountainspring.trip;

import com.mountainspring.media.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByPrimaryImage(Media media);

}
