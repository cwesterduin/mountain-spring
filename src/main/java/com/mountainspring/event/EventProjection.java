package com.mountainspring.event;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonPropertyOrder({"name", "id"})
public interface EventProjection {
    String getId();
    String getName();
    String getDescriptionId();
    Date getDate();
    boolean getCoordinates();
}
