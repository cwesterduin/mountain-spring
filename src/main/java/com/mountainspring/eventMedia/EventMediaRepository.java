package com.mountainspring.eventMedia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventMediaRepository extends JpaRepository<EventMedia, UUID> {

}