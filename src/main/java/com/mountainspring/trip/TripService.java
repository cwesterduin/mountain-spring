package com.mountainspring.trip;

import com.mountainspring.event.EventFrontend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    TripFrontend mapForFrontend(Long id) {
        Trip trip = tripRepository.findById(id).orElse(null);
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

}
