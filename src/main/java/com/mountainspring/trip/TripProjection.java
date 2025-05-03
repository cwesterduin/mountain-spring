package com.mountainspring.trip;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"name", "id"})
public interface TripProjection {
    String getId();
    String getName();
    Date getDate();
}
