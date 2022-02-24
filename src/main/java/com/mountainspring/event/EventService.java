package com.mountainspring.event;

import com.mountainspring.trip.TripFrontend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

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
            eventFrontend.setMedia(event.getMedia());

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

}
