package com.mountainspring.event;

import com.mountainspring.aws.S3Object;
import com.mountainspring.aws.S3ObjectRepository;
import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.eventMedia.EventMediaFrontend;
import com.mountainspring.eventMedia.EventMediaRepository;
import com.mountainspring.trip.Trip;
import com.mountainspring.trip.TripFrontend;
import com.mountainspring.trip.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMediaRepository eventMediaRepository;

    @Autowired
    private S3ObjectRepository s3ObjectRepository;

    @Autowired
    private TripRepository tripRepository;

    EventFrontend mapForFrontend(UUID id) {
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
                    mediaFrontend.setPath(
                            "https://" + media.getMedia().getBucketName() + ".s3." + media.getMedia().getRegion() + ".amazonaws.com/" + media.getMedia().getPath()
                    );
                    mediaFrontend.setDescription(media.getMedia().getDescription());
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

//    public List<EventFrontend> getAllFrontend() {
//        List<EventFrontend> returnAllFrontend = new ArrayList<>();
//        eventRepository.findAll().forEach(event -> {
//            EventFrontend eventFrontend = new EventFrontend();
//            eventFrontend.setId(event.getId());
//            eventFrontend.setName(event.getName());
//            eventFrontend.setRating(event.getRating());
//            eventFrontend.setCoordinates(event.getCoordinates());
//            eventFrontend.setDate(event.getDate());
//            eventFrontend.setDescription(event.getDescription());
//            eventFrontend.setDescriptionId(event.getDescriptionId());
//            eventFrontend.setDistance(event.getDistance());
//            eventFrontend.setElevation(event.getElevation());
//            eventFrontend.setMapFeatures(event.getMapFeatures());
//
//            //media data
//            if (event.getEventMedia() != null) {
//                List<EventMediaFrontend> mediaFrontendList = new ArrayList<>();
//                event.getEventMedia().forEach(media -> {
//                    EventMediaFrontend mediaFrontend = new EventMediaFrontend();
//                    mediaFrontend.setPath(
//                            "https://" + media.getMedia().getBucketName() + ".s3." + media.getMedia().getRegion() + ".amazonaws.com/" + media.getMedia().getPath()
//                    );
//                    mediaFrontend.setSortOrder(media.getSortOrder());
//                    mediaFrontend.setId(media.getId());
//                    mediaFrontend.setMediaId(media.getMedia().getId());
//                    mediaFrontendList.add(mediaFrontend);
//                });
//                eventFrontend.setMedia(mediaFrontendList);
//            }
//
//            //trip data
//            if (event.getTrip() != null) {
//                TripFrontend tripFrontend = new TripFrontend();
//                tripFrontend.setDescription(event.getTrip().getDescription());
//                tripFrontend.setName(event.getTrip().getName());
//                tripFrontend.setId(event.getTrip().getId());
//                tripFrontend.setPrimaryImage(event.getTrip().getPrimaryImage());
//                //set simple ref to other trip events
//                List<EventFrontend> otherEvents = new ArrayList<>();
//                event.getTrip().getEvents().forEach(e -> {
//                    EventFrontend otherEvent = new EventFrontend();
//                    otherEvent.setName(e.getName());
//                    otherEvent.setId(e.getId());
//                    otherEvent.setDate(e.getDate());
//                    otherEvents.add(otherEvent);
//                });
//                tripFrontend.setEvents(otherEvents);
//                //set trip
//                eventFrontend.setTrip(tripFrontend);
//            }
//            returnAllFrontend.add(eventFrontend);
//        });
//        return returnAllFrontend;
//
//    }

    public List<EventProjection> getAllFrontendPreview() {
        return eventRepository.getAllPreview();
    }

    public List<EventFrontendProjection> getAllFrontend() {
        return eventRepository.getAllFrontend();
    }


    public void createOrUpdate(EventFrontend event) {
        Event eventToSave = new Event();

        //simple values
        if (event.getId() != null) {
            eventToSave.setId(event.getId());
        }
        eventToSave.setName(event.getName());
        eventToSave.setDate(event.getDate());
        eventToSave.setCoordinates(event.getCoordinates());
        eventToSave.setDescription(event.getDescription());
        eventToSave.setDescriptionId(event.getDescriptionId());
        eventToSave.setElevation(event.getElevation());
        eventToSave.setDistance(event.getDistance());
        eventToSave.setMapFeatures(event.getMapFeatures());
        eventToSave.setRating(event.getRating());

        //trip
        if (event.getTrip() != null) {
            Optional<Trip> trip = tripRepository.findById(event.getTrip().getId());
            if (trip.isPresent()) {
                eventToSave.setTrip(trip.get());
            } else {
                eventToSave.setTrip(null);
            }
        }

        Event savedEvent = eventRepository.save(eventToSave);


        event.getMedia().forEach(em -> {
            EventMedia eventMediaToSave = new EventMedia();

            if (em.getId() != null) {
                eventMediaToSave.setId(em.getId());
            }
            eventMediaToSave.setSortOrder(em.getSortOrder());
            eventMediaToSave.setEvent(savedEvent);
            Optional<S3Object> s3Object = s3ObjectRepository.findById(em.getMediaId());
            if (s3Object.isPresent()) {
                eventMediaToSave.setMedia(s3Object.get());
            } else {
                eventMediaToSave.setMedia(null);
            }
            eventMediaRepository.save(eventMediaToSave);
        });
    }

    public void deleteOne(UUID id) {
        Optional<Event> myEvent = eventRepository.findById(id);
        if (myEvent.isPresent()) {
            List<EventMedia> eventMedia = eventMediaRepository.findAllByEventId(id);
            eventMediaRepository.deleteAll(eventMedia);
            eventRepository.deleteEventMapFeaturesByEventId(id);
            eventRepository.delete(myEvent.get());
        }
    }
}
