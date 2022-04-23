package com.mountainspring.event;

import com.mountainspring.eventMedia.EventMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMediaRepository eventMediaRepository;

    @Autowired
    private EventService eventService;

    @GetMapping("")
    public List<EventProjection> getAll() {
        return eventService.getAllFrontendPreview();
    }

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody EventFrontend event) {
        eventService.createOrUpdate(event);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable UUID id) {
        if (eventRepository.existsById(id)) {
            return new ResponseEntity<>(
                    eventService.mapForFrontend(id),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable UUID id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/media/{id}")
    public ResponseEntity<?> deleteOneEventMedia(
            @PathVariable UUID id
    ) {
        if (eventMediaRepository.existsById(id)) {
            eventMediaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
