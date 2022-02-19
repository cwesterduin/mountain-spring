package com.mountainspring.controllers;

import com.mountainspring.models.Event;
import com.mountainspring.models.Geo.Point;
import com.mountainspring.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/events")
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (eventRepository.existsById(id)) {
            return new ResponseEntity<>(eventRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/events", consumes = "application/json")
    public void saveNew(@RequestBody Event event) {
        eventRepository.save(event);
    }

}
