package com.mountainspring.media;

import com.mountainspring.event.Event;
import com.mountainspring.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private MediaService mediaService;

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody Media media) {
        mediaRepository.save(media);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (mediaRepository.existsById(id)) {
            return new ResponseEntity<>(mediaRepository.findById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        if (mediaRepository.existsById(id)) {
            mediaService.cascadeDeleteMedia(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
