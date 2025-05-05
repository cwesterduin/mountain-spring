package com.mountainspring.event;

import com.mountainspring.aws.S3ObjectRepository;
import com.mountainspring.eventMedia.EventMediaRepository;
import com.mountainspring.trip.TripRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMediaRepository eventMediaRepository;

    @Mock
    private S3ObjectRepository s3ObjectRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    public void should_create_event() {
        Event event = new Event();
        event.setEventMedia(Collections.emptyList());
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        EventFrontend eventFrontend = new EventFrontend();
        eventService.createOrUpdate(eventFrontend);
        verify(eventRepository, times(1)).save(any(Event.class));
    }
}
