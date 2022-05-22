package com.mountainspring.mapFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MapFeatureService {

    @Autowired
    private MapFeatureRepository mapFeatureRepository;

    public void deleteOne(UUID id) {
        if (mapFeatureRepository.findById(id).isPresent()) {
            mapFeatureRepository.delete(mapFeatureRepository.findById(id).get());
        }
    }

}
