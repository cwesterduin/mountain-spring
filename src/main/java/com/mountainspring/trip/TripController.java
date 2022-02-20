package com.mountainspring.trip;

import com.mountainspring.trip.Trip;
import com.mountainspring.trip.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripService tripService;

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody Trip trip) {
        tripRepository.save(trip);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (tripRepository.existsById(id)) {
            return new ResponseEntity<>(
                    tripService.mapForFrontend(id),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
