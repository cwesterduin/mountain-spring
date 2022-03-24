package com.mountainspring.trip;

import com.mountainspring.aws.S3Object;
import com.mountainspring.event.EventFrontend;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TripFrontend {
    private UUID id;
    private String name;
    private String description;
    private S3Object primaryImage;
    private List<EventFrontend> events;
}
