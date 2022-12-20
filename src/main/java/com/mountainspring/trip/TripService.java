package com.mountainspring.trip;

import com.mountainspring.event.Event;
import com.mountainspring.event.EventFrontend;
import com.mountainspring.event.EventRepository;
import com.mountainspring.models.Point;
import net.bytebuddy.matcher.FilterableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private EventRepository eventRepository;

    TripFrontend mapForFrontend(UUID id) {
        Trip trip = tripRepository.findById(id).orElse(null);
        return tripMapper(trip);
    }

    private TripFrontend tripMapper(Trip trip) {
        TripFrontend tripFrontend = new TripFrontend();
        if (trip != null) {
            tripFrontend.setName(trip.getName());
            tripFrontend.setPrimaryImage(trip.getPrimaryImage());
            tripFrontend.setId(trip.getId());
            tripFrontend.setDescription(trip.getDescription());

            if (trip.getEvents() != null) {
                List<EventFrontend> frontEndEvents = new ArrayList<>();
                trip.getEvents().forEach(e -> {

                        List<Point> coordinates = null;
                        if (e.getCoordinates() != null) {
                            coordinates = Collections.singletonList(e.getCoordinates().get(0));
                        }

                        EventFrontend eventFrontend = new EventFrontend();
                        eventFrontend.setId(e.getId());
                        eventFrontend.setName(e.getName());
                        eventFrontend.setDate(e.getDate());
                        eventFrontend.setDistance(e.getDistance());
                        eventFrontend.setElevation(e.getElevation());
                        eventFrontend.setCoordinates(coordinates);
                        frontEndEvents.add(eventFrontend);
                        }
                );
                tripFrontend.setEvents(frontEndEvents);
            }
        }
        return tripFrontend;
    }

    List<TripFrontend> mapAllForFrontend() {
        List<Trip> allTrips = tripRepository.findAll();
        List<TripFrontend> allTripsFrontend = new ArrayList<>();
        for (Trip trip : allTrips) {
            allTripsFrontend.add(tripMapper(trip));
        }
        return allTripsFrontend;
    }

    public List<TripProjection> getAllPreview() {
        return tripRepository.getAllPreview();
    }

    public void deleteOne(UUID id) {
        if (tripRepository.findById(id).isPresent()) {
            List<Event> eventsWithTrip = eventRepository.findAllByTripId(id);
            eventsWithTrip.forEach(e -> e.setTrip(null));
            eventRepository.saveAll(eventsWithTrip);
            tripRepository.delete(tripRepository.findById(id).get());
        }
    }
}
