package com.mountainspring.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaRepository mediaRepository;

    @PostMapping(value = "", consumes = "application/json")
    public void saveNew(@RequestBody Media media) {
        mediaRepository.save(media);
    }

}
