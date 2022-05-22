package com.mountainspring.trip;

import com.mountainspring.trip.Trip;
import com.mountainspring.trip.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripService tripService;

    @GetMapping("")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(
                tripService.getAllPreview(),
                HttpStatus.OK
        );
    }

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody Trip trip) {
        tripRepository.save(trip);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        if (tripRepository.existsById(id)) {
            return new ResponseEntity<>(
                    tripService.mapForFrontend(id),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID id) {
        if (tripRepository.existsById(id)) {
            tripService.deleteOne(id);
            return new ResponseEntity<>(
                    HttpStatus.ACCEPTED
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
