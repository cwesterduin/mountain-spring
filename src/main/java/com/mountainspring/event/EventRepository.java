package com.mountainspring.event;

import com.mountainspring.media.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByMedia(Media media);

}
