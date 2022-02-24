package com.mountainspring.mapFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/map-features")
public class MapFeatureController {

    @Autowired
    private MapFeatureRepository mapFeatureRepository;

    @GetMapping(value = "")
    public List<MapFeature> getAll() {
        return mapFeatureRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Optional<MapFeature> getOne(@PathVariable Long id) {
        return mapFeatureRepository.findById(id);
    }

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody MapFeature mapFeature) {
        mapFeatureRepository.save(mapFeature);
    }

    @DeleteMapping(value = "/{id}", consumes = "application/json")
    public void deleteOne(@PathVariable Long id) {
        mapFeatureRepository.deleteById(id);
    }

}
