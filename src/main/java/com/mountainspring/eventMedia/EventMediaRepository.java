package com.mountainspring.eventMedia;

import com.mountainspring.event.EventService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventMediaRepository extends JpaRepository<EventMedia, UUID> {

    List<EventMedia> findAllByEventId(UUID id);
}