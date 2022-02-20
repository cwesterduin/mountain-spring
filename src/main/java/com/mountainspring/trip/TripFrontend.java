package com.mountainspring.trip;

import com.mountainspring.event.Event;
import com.mountainspring.event.EventFrontend;
import com.mountainspring.media.Media;
import lombok.Data;

import java.util.List;

@Data
public class TripFrontend {
    private Long id;
    private String name;
    private String description;
    private Media primaryImage;
    private List<EventFrontend> events;
}
