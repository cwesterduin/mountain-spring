package com.mountainspring.trip;

import com.mountainspring.event.EventFrontend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

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
                        EventFrontend eventFrontend = new EventFrontend();
                        eventFrontend.setId(e.getId());
                        eventFrontend.setName(e.getName());
                        eventFrontend.setDate(e.getDate());
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
}
