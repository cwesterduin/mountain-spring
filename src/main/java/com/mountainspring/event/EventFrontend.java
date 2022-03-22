package com.mountainspring.event;

import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.eventMedia.EventMediaFrontend;
import com.mountainspring.mapFeature.MapFeature;
import com.mountainspring.media.Media;
import com.mountainspring.models.Point;
import com.mountainspring.trip.TripFrontend;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventFrontend {
    private Long id;
    private String name;
    private Date date;
    private List<Point> coordinates;
    private String descriptionId;
    private String description;
    private Integer rating;
    private Double elevation;
    private Double distance;
    private List<EventMediaFrontend> media;
    private List<MapFeature> mapFeatures;
    private TripFrontend trip;
}
