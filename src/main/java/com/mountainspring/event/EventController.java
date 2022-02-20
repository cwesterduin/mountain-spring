package com.mountainspring.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("")
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (eventRepository.existsById(id)) {
            return new ResponseEntity<>(eventRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody Event event) {
        eventRepository.save(event);
    }

}
