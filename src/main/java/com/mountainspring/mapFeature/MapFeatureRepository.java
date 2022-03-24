package com.mountainspring.mapFeature;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MapFeatureRepository extends JpaRepository<MapFeature, UUID> {

}
