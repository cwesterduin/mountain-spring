package com.mountainspring.mapFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/map-features")
public class MapFeatureController {

    @Autowired
    private MapFeatureRepository mapFeatureRepository;

    @GetMapping(value = "")
    public List<MapFeatureProjection> getAll() {
        return mapFeatureRepository.getAllPreview();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        if (mapFeatureRepository.existsById(id)) {
            return new ResponseEntity<>(
                    mapFeatureRepository.findById(id),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody MapFeature mapFeature) {
        mapFeatureRepository.save(mapFeature);
    }

    @DeleteMapping(value = "/{id}", consumes = "application/json")
    public void deleteOne(@PathVariable UUID id) {
        mapFeatureRepository.deleteById(id);
    }

}
