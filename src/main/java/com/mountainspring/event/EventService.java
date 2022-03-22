package com.mountainspring.event;

import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.eventMedia.EventMediaFrontend;
import com.mountainspring.eventMedia.EventMediaRepository;
import com.mountainspring.trip.TripFrontend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMediaRepository eventMediaRepository;

    EventFrontend mapForFrontend(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        EventFrontend eventFrontend = new EventFrontend();
        if (event != null) {

            //event data
            eventFrontend.setId(event.getId());
            eventFrontend.setName(event.getName());
            eventFrontend.setRating(event.getRating());
            eventFrontend.setCoordinates(event.getCoordinates());
            eventFrontend.setDate(event.getDate());
            eventFrontend.setDescription(event.getDescription());
            eventFrontend.setDescriptionId(event.getDescriptionId());
            eventFrontend.setDistance(event.getDistance());
            eventFrontend.setElevation(event.getElevation());
            eventFrontend.setMapFeatures(event.getMapFeatures());

            //media data
            if (event.getEventMedia() != null) {
                List<EventMediaFrontend> mediaFrontendList = new ArrayList<>();
                event.getEventMedia().forEach(media -> {
                    EventMediaFrontend mediaFrontend = new EventMediaFrontend();
                    mediaFrontend.setPath(media.getMedia().getPath());
                    mediaFrontend.setSortOrder(media.getSortOrder());
                    mediaFrontend.setId(media.getId());
                    mediaFrontend.setMediaId(media.getMedia().getId());
                    mediaFrontendList.add(mediaFrontend);
                });
                eventFrontend.setMedia(mediaFrontendList);
            }

            //trip data
            if (event.getTrip() != null) {
                TripFrontend tripFrontend = new TripFrontend();
                tripFrontend.setDescription(event.getTrip().getDescription());
                tripFrontend.setName(event.getTrip().getName());
                tripFrontend.setId(event.getTrip().getId());
                tripFrontend.setPrimaryImage(event.getTrip().getPrimaryImage());
                //set simple ref to other trip events
                List<EventFrontend> otherEvents = new ArrayList<>();
                event.getTrip().getEvents().forEach(e -> {
                    EventFrontend otherEvent = new EventFrontend();
                    otherEvent.setName(e.getName());
                    otherEvent.setId(e.getId());
                    otherEvent.setDate(e.getDate());
                    otherEvents.add(otherEvent);
                });
                tripFrontend.setEvents(otherEvents);
                //set trip
                eventFrontend.setTrip(tripFrontend);
            }

        }
        return eventFrontend;
    }

    public List<EventFrontend> getAllFrontend() {
        List<EventFrontend> returnAllFrontend = new ArrayList<>();
        eventRepository.findAll().forEach(event -> {
            EventFrontend eventFrontend = new EventFrontend();
            eventFrontend.setId(event.getId());
            eventFrontend.setName(event.getName());
            eventFrontend.setRating(event.getRating());
            eventFrontend.setCoordinates(event.getCoordinates());
            eventFrontend.setDate(event.getDate());
            eventFrontend.setDescription(event.getDescription());
            eventFrontend.setDescriptionId(event.getDescriptionId());
            eventFrontend.setDistance(event.getDistance());
            eventFrontend.setElevation(event.getElevation());
            eventFrontend.setMapFeatures(event.getMapFeatures());

            //media data
            if (event.getEventMedia() != null) {
                List<EventMediaFrontend> mediaFrontendList = new ArrayList<>();
                event.getEventMedia().forEach(media -> {
                    EventMediaFrontend mediaFrontend = new EventMediaFrontend();
                    mediaFrontend.setPath(media.getMedia().getPath());                    mediaFrontend.setSortOrder(media.getSortOrder());
                    mediaFrontend.setId(media.getId());
                    mediaFrontend.setMediaId(media.getMedia().getId());
                    mediaFrontendList.add(mediaFrontend);
                });
                eventFrontend.setMedia(mediaFrontendList);
            }

            //trip data
            if (event.getTrip() != null) {
                TripFrontend tripFrontend = new TripFrontend();
                tripFrontend.setDescription(event.getTrip().getDescription());
                tripFrontend.setName(event.getTrip().getName());
                tripFrontend.setId(event.getTrip().getId());
                tripFrontend.setPrimaryImage(event.getTrip().getPrimaryImage());
                //set simple ref to other trip events
                List<EventFrontend> otherEvents = new ArrayList<>();
                event.getTrip().getEvents().forEach(e -> {
                    EventFrontend otherEvent = new EventFrontend();
                    otherEvent.setName(e.getName());
                    otherEvent.setId(e.getId());
                    otherEvent.setDate(e.getDate());
                    otherEvents.add(otherEvent);
                });
                tripFrontend.setEvents(otherEvents);
                //set trip
                eventFrontend.setTrip(tripFrontend);
            }
            returnAllFrontend.add(eventFrontend);
        });
        return returnAllFrontend;

    }

    public void createOrUpdate(Event event) {
        Optional<Event> resultEvent = eventRepository.findById(event.getId());
        resultEvent.ifPresent(eventToSave -> {
            eventRepository.save(eventToSave);
        });
        event.getEventMedia().forEach(em -> {
            Optional<EventMedia> eventMedia = eventMediaRepository.findById(em.getId());
            eventMedia.ifPresent(emp -> {
                emp.setSortOrder(em.getSortOrder());
                eventMediaRepository.save(emp);
            });
        });
    }
}
