package com.mountainspring.media;

import com.mountainspring.event.Event;
import com.mountainspring.event.EventRepository;
import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.trip.Trip;
import com.mountainspring.trip.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TripRepository tripRepository;

//    public void cascadeDeleteMedia(Long id) {
//        Optional<Media> media = mediaRepository.findById(id);
//        //find any events including this media and remove reference
//        List<Event> events = eventRepository.findAllByMedia(media.orElse(null));
//        events.forEach(e -> {
//                    List<EventMedia> eventMediaInitial = e.getEventMedia();
//                    eventMediaInitial.remove(media.orElse(null));
//                    e.setEventMedia(eventMediaInitial);
//                    eventRepository.save(e);
//                });
//        //find any trips with this media and remove reference
//        List<Trip> trips = tripRepository.findAllByPrimaryImage(media.orElse(null));
//        trips.forEach(t -> t.setPrimaryImage(null));
//        //delete media entity
//        mediaRepository.deleteById(id);
//    }


}
